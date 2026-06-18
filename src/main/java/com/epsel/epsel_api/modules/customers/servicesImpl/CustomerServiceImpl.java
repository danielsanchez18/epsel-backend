package com.epsel.epsel_api.modules.customers.servicesImpl;

import com.epsel.epsel_api.modules.auth.utils.AuthUtils;
import com.epsel.epsel_api.modules.customers.dto.CreateCustomerDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerResponseDTO;
import com.epsel.epsel_api.modules.customers.dto.UpdateCustomerDTO;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.customers.services.CustomerService;
import com.epsel.epsel_api.modules.customers.specifications.CustomerSpecification;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import com.epsel.epsel_api.modules.customers.dto.CustomerKpisDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerDetailKpisDTO;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.payments.repositories.PaymentRepository;
import com.epsel.epsel_api.modules.incidents.repository.IncidentRepository;
import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final BillingRepository billingRepository;
    private final PaymentRepository paymentRepository;
    private final IncidentRepository incidentRepository;
    private final AuthUtils authUtils;

    @Override
    public CustomerResponseDTO create(CreateCustomerDTO dto) {

        if (repository.existsByDocumentNumberAndDeletedFalse(dto.getDocumentNumber())) {
            throw new BadRequestException("Número de documento ya registrado");
        }

        Customer customer = new Customer();

        customer.setType(dto.getType());
        customer.setDocumentNumber(dto.getDocumentNumber());
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());

        try {
            User currentUser = authUtils.getCurrentUser();
            customer.setCreatedBy(currentUser);
            customer.setUpdatedBy(currentUser);
        } catch (Exception e) {
            // Fallback for seeders/tests
        }

        Customer saved = repository.save(customer);
        return mapResponse(saved);
    }

    @Override
    public CustomerResponseDTO update(UUID id, UpdateCustomerDTO dto) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (dto.getFullName() != null) { customer.setFullName(dto.getFullName()); }
        if (dto.getPhone() != null) { customer.setPhone(dto.getPhone()); }
        if (dto.getEmail() != null) { customer.setEmail(dto.getEmail()); }

        try {
            User currentUser = authUtils.getCurrentUser();
            customer.setUpdatedBy(currentUser);
        } catch (Exception e) {
            // Fallback for seeders/tests
        }

        Customer saved = repository.save(customer);
        return mapResponse(saved);
    }

    @Override
    public CustomerResponseDTO getById(UUID id) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        return mapResponse(customer);
    }

    @Override
    public Page<CustomerResponseDTO> search(
            String search, 
            CustomerType type, 
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            Pageable pageable) {

        return repository.findAll(
                CustomerSpecification.search(search, type, startDate, endDate), pageable
        ).map(this::mapResponse);
    }

    @Override
    public void delete(UUID id) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        customer.setDeleted(true);
        repository.save(customer);
    }

    private CustomerResponseDTO mapResponse(Customer customer) {
        String createdByName = "Sistema";
        String updatedByName = "Sistema";

        if (customer.getCreatedBy() != null) {
            createdByName = customer.getCreatedBy().getNames() + " " + customer.getCreatedBy().getLastNames();
        }
        if (customer.getUpdatedBy() != null) {
            updatedByName = customer.getUpdatedBy().getNames() + " " + customer.getUpdatedBy().getLastNames();
        }

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .type(customer.getType())
                .documentNumber(customer.getDocumentNumber())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .createdBy(customer.getCreatedBy() != null ? customer.getCreatedBy().getId() : null)
                .updatedBy(customer.getUpdatedBy() != null ? customer.getUpdatedBy().getId() : null)
                .createdByName(createdByName)
                .updatedByName(updatedByName)
                .build();
    }

    @Override
    public CustomerKpisDTO getKpis() {
        long totalCustomers = repository.countByDeletedFalse();

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        long customersChangeThisMonth = repository.countCreatedInMonth(month, year);

        long activeCustomers = repository.countActiveCustomers();
        double activeCustomersPercentage = totalCustomers > 0 
                ? (double) activeCustomers * 100.0 / totalCustomers 
                : 0.0;

        long delinquentCustomers = billingRepository.countDelinquentCustomers();
        java.math.BigDecimal delinquentAmount = billingRepository.sumDelinquentAmount();

        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        long newCustomersLast30Days = repository.countByDeletedFalseAndCreatedAtAfter(last30Days);

        return CustomerKpisDTO.builder()
                .totalCustomers(totalCustomers)
                .customersChangeThisMonth(customersChangeThisMonth)
                .activeCustomers(activeCustomers)
                .activeCustomersPercentage(activeCustomersPercentage)
                .delinquentCustomers(delinquentCustomers)
                .delinquentAmount(delinquentAmount)
                .newCustomersLast30Days(newCustomersLast30Days)
                .build();
    }

    @Override
    public CustomerDetailKpisDTO getDetailKpis(UUID customerId) {
        repository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        BigDecimal totalDebt = billingRepository.sumPendingAmountByCustomerId(customerId);
        long overdueBillsCount = billingRepository.countOverdueBillsByCustomerId(customerId);
        double averageConsumption = billingRepository.averageConsumptionByCustomerId(customerId);

        double consumptionChangePercentage = 0.0;
        List<Integer> consumptions = billingRepository.findConsumptionsByCustomerIdOrderByDateDesc(
                customerId, PageRequest.of(0, 2)
        );
        if (consumptions.size() >= 2) {
            int latest = consumptions.get(0);
            int previous = consumptions.get(1);
            if (previous > 0) {
                consumptionChangePercentage = (double) (latest - previous) * 100.0 / previous;
            }
        }

        LocalDate lastPaymentDate = null;
        boolean lastPaymentDelayed = false;
        List<Payment> payments = paymentRepository.findLatestCompletedPaymentByCustomerId(
                customerId, PageRequest.of(0, 1)
        );
        if (!payments.isEmpty()) {
            Payment lastPayment = payments.get(0);
            lastPaymentDate = lastPayment.getPaymentDate().toLocalDate();
            if (lastPayment.getBilling() != null && lastPayment.getBilling().getDueDate() != null) {
                lastPaymentDelayed = lastPaymentDate.isAfter(lastPayment.getBilling().getDueDate());
            }
        }

        long activeIncidentsCount = incidentRepository.countByCustomerIdAndStatusInAndDeletedFalse(
                customerId, List.of(IncidentStatus.OPEN, IncidentStatus.IN_PROGRESS)
        );

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentIncidentsCount = incidentRepository.countByCustomerIdAndDeletedFalseAndReportedDateAfter(
                customerId, thirtyDaysAgo
        );

        return CustomerDetailKpisDTO.builder()
                .totalDebt(totalDebt)
                .overdueBillsCount(overdueBillsCount)
                .averageConsumption(averageConsumption)
                .consumptionChangePercentage(consumptionChangePercentage)
                .lastPaymentDate(lastPaymentDate)
                .lastPaymentDelayed(lastPaymentDelayed)
                .activeIncidentsCount(activeIncidentsCount)
                .recentIncidentsCount(recentIncidentsCount)
                .build();
    }
    @Override
    public com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateCustomerDTO> previewImport(org.springframework.web.multipart.MultipartFile file) {
        if (!com.epsel.epsel_api.shared.utils.ExcelImportHelper.hasExcelFormat(file)) {
            throw new com.epsel.epsel_api.shared.exceptions.BadRequestException("Formato de archivo inválido. Por favor suba un archivo Excel o CSV.");
        }

        java.util.List<java.util.List<String>> rows = com.epsel.epsel_api.shared.utils.ExcelImportHelper.readExcel(file);
        
        java.util.List<CreateCustomerDTO> validData = new java.util.ArrayList<>();
        java.util.List<com.epsel.epsel_api.shared.responses.ImportErrorDTO> errors = new java.util.ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            java.util.List<String> row = rows.get(i);
            int rowNum = i + 2; 
            java.util.List<String> rowErrors = new java.util.ArrayList<>();
            
            // Expected columns: Tipo(PERSON/COMPANY), DNI/RUC, Nombre/Razón Social, Teléfono, Email
            if (row.size() < 5) {
                rowErrors.add("La fila no contiene todas las columnas requeridas (Tipo, Documento, Nombre Completo, Teléfono, Email)");
                errors.add(new com.epsel.epsel_api.shared.responses.ImportErrorDTO(rowNum, rowErrors));
                continue;
            }

            String typeStr = row.get(0);
            String documentNumber = row.get(1);
            String fullName = row.get(2);
            String phone = row.get(3);
            String email = row.get(4);

            CustomerType type = null;
            if (typeStr != null && (typeStr.equalsIgnoreCase("PERSON") || typeStr.equalsIgnoreCase("PERSONA"))) {
                type = CustomerType.PERSON;
            } else if (typeStr != null && (typeStr.equalsIgnoreCase("COMPANY") || typeStr.equalsIgnoreCase("EMPRESA"))) {
                type = CustomerType.COMPANY;
            } else {
                rowErrors.add("Tipo de cliente inválido (Debe ser PERSON/PERSONA o COMPANY/EMPRESA)");
            }

            if (documentNumber == null || documentNumber.isEmpty()) {
                rowErrors.add("El número de documento es requerido");
            } else if (repository.existsByDocumentNumberAndDeletedFalse(documentNumber)) {
                rowErrors.add("El número de documento ya se encuentra registrado");
            }

            if (fullName == null || fullName.isEmpty()) {
                rowErrors.add("El nombre completo o razón social es requerido");
            }

            if (rowErrors.isEmpty()) {
                CreateCustomerDTO dto = new CreateCustomerDTO();
                dto.setType(type);
                dto.setDocumentNumber(documentNumber);
                dto.setFullName(fullName);
                dto.setPhone(phone);
                dto.setEmail(email);
                validData.add(dto);
            } else {
                errors.add(new com.epsel.epsel_api.shared.responses.ImportErrorDTO(rowNum, rowErrors));
            }
        }

        return com.epsel.epsel_api.shared.responses.ImportPreviewResponse.<CreateCustomerDTO>builder()
                .totalRows(rows.size())
                .validCount(validData.size())
                .invalidCount(errors.size())
                .validData(validData)
                .errors(errors)
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void createBulk(java.util.List<CreateCustomerDTO> dtos) {
        for (CreateCustomerDTO dto : dtos) {
            if (repository.existsByDocumentNumberAndDeletedFalse(dto.getDocumentNumber())) {
                continue; 
            }
            create(dto);
        }
    }
}