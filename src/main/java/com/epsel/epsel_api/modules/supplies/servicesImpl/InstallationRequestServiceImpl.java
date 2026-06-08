package com.epsel.epsel_api.modules.supplies.servicesImpl;

import com.epsel.epsel_api.modules.auth.utils.AuthUtils;
import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceFeeConfigurationRepository;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplies.services.InstallationRequestService;
import com.epsel.epsel_api.modules.supplies.specifications.InstallationRequestSpecification;
import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.repository.SupplyOperationRepository;
import com.epsel.epsel_api.modules.supplyWorkOrder.entity.SupplyWorkOrder;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.epsel.epsel_api.modules.supplies.dto.ApplicationKpisDTO;
import com.epsel.epsel_api.shared.responses.ImportErrorDTO;
import com.epsel.epsel_api.shared.responses.ImportPreviewResponse;
import com.epsel.epsel_api.shared.utils.ExcelImportHelper;

@Service
@RequiredArgsConstructor
public class InstallationRequestServiceImpl implements InstallationRequestService {

    private final InstallationRequestRepository repository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final ServiceFeeConfigurationRepository feeRepository;
    private final SupplyRepository supplyRepository;
    private final AuthUtils authUtils;
    private final SupplyOperationRepository operationRepository;
    private final SupplyWorkOrderRepository supplyWorkOrderRepository;

    @Override
    public InstallationRequestResponseDTO create(
            CreateInstallationRequestDTO dto
    ) {

        Customer customer = customerRepository
                .findByIdAndDeletedFalse(dto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente no encontrado"));

        Property property = propertyRepository
                .findByIdAndDeletedFalse(dto.getPropertyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Propiedad no encontrada"));

        String internalReference = dto.getInternalReference() != null
                ? dto.getInternalReference().trim().toUpperCase()
                : "";

        Boolean exists = repository
                .existsByPropertyAndInternalReferenceIgnoreCaseAndStatusIn(
                        property,
                        internalReference,
                        List.of(
                                InstallationRequestStatus.PENDING,
                                InstallationRequestStatus.APPROVED,
                                InstallationRequestStatus.INSTALLED
                        )
                );

        if (Boolean.TRUE.equals(exists)) {
            throw new BadRequestException(
                    "Ya existe una instalación registrada para esta referencia"
            );
        }

        ServiceFeeConfiguration fee = feeRepository
                .findByZone_IdAndFeeTypeAndActiveTrue(
                        property.getZone().getId(),
                        ServiceFeeType.INSTALLATION
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instalación no disponible para esta zona"
                        ));

        InstallationRequest request = new InstallationRequest();

        request.setCustomer(customer);
        request.setProperty(property);
        request.setInternalReference(internalReference);
        request.setInstallationCost(fee.getAmount());
        request.setStatus(InstallationRequestStatus.PENDING);
        request.setRequestedDate(dto.getRequestedDate());
        request.setObservations(dto.getObservations());

        InstallationRequest saved = repository.save(request);

        return mapResponse(saved);
    }

    @Override
    public InstallationRequestResponseDTO approve(UUID id) {

        InstallationRequest request = repository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden aprobar solicitudes pendientes");
        }

        request.setStatus(InstallationRequestStatus.APPROVED);
        request.setApprovedDate(LocalDate.now());
        request.setApprovedBy(authUtils.getCurrentUser());

        InstallationRequest saved = repository.save(request);

        // Auto create supply with PENDING_INSTALLATION status
        Supply supply = new Supply();
        supply.setProperty(saved.getProperty());
        supply.setCustomer(saved.getCustomer());
        supply.setInstallationRequest(saved);
        supply.setStatus(SupplyStatus.PENDING_INSTALLATION);
        supply.setConnected(false);
        supply.setSupplyType(saved.getProperty().getType());
        supply.setSupplyNumber(generateSupplyNumber());
        supply.setInternalReference(saved.getInternalReference());
        supply.setInstallationDate(LocalDate.now());
        supply.setLastReading(0);
        supply.setMeterNumber("PEND_INSTALL_" + supply.getSupplyNumber());
        
        Supply savedSupply = supplyRepository.save(supply);

        return mapResponse(saved);
    }

    @Override
    public InstallationRequestResponseDTO reject(UUID id, String observations) {

        InstallationRequest request = repository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden rechazar solicitudes pendientes");
        }

        request.setStatus(InstallationRequestStatus.REJECTED);
        request.setObservations(observations);
        request.setRejectedBy(authUtils.getCurrentUser());

        InstallationRequest saved = repository.save(request);
        return mapResponse(saved);
    }

    @Override
    public InstallationRequestResponseDTO install(UUID id, InstallSupplyDTO dto) {

        InstallationRequest request = repository.findByIdAndDeletedFalse(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.APPROVED) {
            throw new BadRequestException("Solo se pueden instalar solicitudes aprobadas");
        }

        // Validar que el meterNumber no exista ya
        if (supplyRepository.existsByMeterNumberAndDeletedFalse(dto.getMeterNumber())) {
            throw new BadRequestException("El número de medidor ya está registrado en el sistema");
        }

        Supply supply = new Supply();

        supply.setProperty(request.getProperty());
        supply.setCustomer(request.getCustomer());
        supply.setInstallationRequest(request);
        supply.setStatus(SupplyStatus.ACTIVE);
        supply.setConnected(true);
        supply.setSupplyType(request.getProperty().getType());
        supply.setSupplyNumber(generateSupplyNumber());
        supply.setInternalReference(request.getInternalReference());
        supply.setInstallationDate(LocalDate.now());
        supply.setActivationDate(LocalDate.now());
        supply.setLastReading(0);
        supply.setMeterNumber(dto.getMeterNumber());


        supplyRepository.save(supply);

        registerOperation(
                supply,
                SupplyOperationType.INSTALLATION,
                null,
                "Instalación inicial del suministro"
        );

        request.setStatus(InstallationRequestStatus.INSTALLED);
        request.setInstallationDate(LocalDate.now());
        request.setInstalledBy(authUtils.getCurrentUser());

        InstallationRequest saved = repository.save(request);
        return mapResponse(saved);
    }

    @Override
    public Page<InstallationRequestResponseDTO> findAll(
            String search,
            InstallationRequestStatus status,
            String zoneName,
            Pageable pageable
    ) {

        return repository.findAll(
                InstallationRequestSpecification.search(
                        search,
                        status,
                        zoneName
                ),
                pageable
        ).map(this::mapResponse);
    }

    @Override
    public InstallationRequestResponseDTO getById(
            UUID id
    ) {

        InstallationRequest request = repository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Solicitud no encontrada"
                        )
                );

        return mapResponse(request);
    }

    private String generateSupplyNumber() {
        long count = supplyRepository.count() + 1;
        return String.format("SUM-%08d", count);
    }

    private InstallationRequestResponseDTO mapResponse(InstallationRequest request) {
        return InstallationRequestResponseDTO.builder()
                .id(request.getId())
                .customerId(request.getCustomer() != null ? request.getCustomer().getId() : null)
                .customerName(request.getCustomer() != null ? request.getCustomer().getFullName() : null)
                .zoneName(request.getProperty() != null && request.getProperty().getZone() != null ? request.getProperty().getZone().getName() : null)
                .propertyId(request.getProperty() != null ? request.getProperty().getId() : null)
                .propertyAddress(request.getProperty() != null ? request.getProperty().getAddress() : null)
                .internalReference(request.getInternalReference())
                .installationCost(request.getInstallationCost())
                .status(request.getStatus())
                .requestedDate(request.getRequestedDate())
                .approvedDate(request.getApprovedDate())
                .installationDate(request.getInstallationDate())
                .rejectedDate(request.getRejectedDate())
                .approvedBy(request.getApprovedBy() != null ? (
                        (request.getApprovedBy().getNames() != null ? request.getApprovedBy().getNames() : "") +
                                (request.getApprovedBy().getLastNames() != null ? " " + request.getApprovedBy().getLastNames() : "")
                ) : null)
                .installedBy(request.getInstalledBy() != null ? (
                        (request.getInstalledBy().getNames() != null ? request.getInstalledBy().getNames() : "") +
                                (request.getInstalledBy().getLastNames() != null ? " " + request.getInstalledBy().getLastNames() : "")
                ) : null)
                .rejectedBy(request.getRejectedBy() != null ? (
                        (request.getRejectedBy().getNames() != null ? request.getRejectedBy().getNames() : "") +
                                (request.getRejectedBy().getLastNames() != null ? " " + request.getRejectedBy().getLastNames() : "")
                ) : null)
                .observations(request.getObservations())
                .build();
    }


    @Override
    public ImportPreviewResponse<CreateInstallationRequestDTO> previewImport(MultipartFile file) {
        if (!ExcelImportHelper.hasExcelFormat(file)) {
            throw new BadRequestException("Formato de archivo inválido. Por favor suba un archivo Excel o CSV.");
        }

        List<List<String>> rows = ExcelImportHelper.readExcel(file);

        List<CreateInstallationRequestDTO> validData = new ArrayList<>();
        List<ImportErrorDTO> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            int rowNumber = i + 2; // +1 for header, +1 for 0-index
            
            List<String> rowErrors = new ArrayList<>();

            if (row.size() < 2) {
                rowErrors.add("La fila no contiene las columnas mínimas requeridas (Documento Cliente, Código Catastral)");
                errors.add(new ImportErrorDTO(rowNumber, rowErrors));
                continue;
            }

            String documentNumber = row.get(0);
            String cadastralCode = row.get(1);
            String internalReference = row.size() > 2 ? row.get(2) : "";
            String reqDateStr = row.size() > 3 ? row.get(3) : "";
            String observations = row.size() > 4 ? row.get(4) : "";

            if (documentNumber == null || documentNumber.trim().isEmpty()) {
                rowErrors.add("Documento Cliente es requerido");
            }
            if (cadastralCode == null || cadastralCode.trim().isEmpty()) {
                rowErrors.add("Código Catastral es requerido");
            }
            
            Customer customer = null;
            if (documentNumber != null && !documentNumber.trim().isEmpty()) {
                customer = customerRepository.findByDocumentNumberAndDeletedFalse(documentNumber.trim()).orElse(null);
                if (customer == null) {
                    rowErrors.add("Cliente con documento " + documentNumber + " no encontrado");
                }
            }

            Property property = null;
            if (cadastralCode != null && !cadastralCode.trim().isEmpty()) {
                property = propertyRepository.findByCadastralCode(cadastralCode.trim()).orElse(null);
                if (property == null) {
                    rowErrors.add("Propiedad con código catastral " + cadastralCode + " no encontrada");
                }
            }
            
            LocalDate requestedDate = LocalDate.now();
            if (reqDateStr != null && !reqDateStr.trim().isEmpty()) {
                try {
                    requestedDate = LocalDate.parse(reqDateStr.trim());
                } catch (Exception e) {
                    rowErrors.add("Fecha inválida. Use YYYY-MM-DD");
                }
            }
            
            if (rowErrors.isEmpty() && property != null) {
                Boolean exists = repository.existsByPropertyAndInternalReferenceIgnoreCaseAndStatusIn(
                        property,
                        internalReference != null ? internalReference.trim().toUpperCase() : "",
                        List.of(
                                InstallationRequestStatus.PENDING,
                                InstallationRequestStatus.APPROVED,
                                InstallationRequestStatus.INSTALLED
                        )
                );
                if (Boolean.TRUE.equals(exists)) {
                    rowErrors.add("Ya existe una instalación registrada para esta referencia y predio");
                }
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportErrorDTO(rowNumber, rowErrors));
            } else {
                CreateInstallationRequestDTO dto = new CreateInstallationRequestDTO();
                dto.setCustomerId(customer.getId());
                dto.setPropertyId(property.getId());
                dto.setInternalReference(internalReference != null ? internalReference.trim() : "");
                dto.setRequestedDate(requestedDate);
                dto.setObservations(observations != null ? observations.trim() : "");
                validData.add(dto);
            }
        }
        
        return ImportPreviewResponse.<CreateInstallationRequestDTO>builder()
                .totalRows(rows.size())
                .validCount(validData.size())
                .invalidCount(errors.size())
                .validData(validData)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public void createBulk(List<CreateInstallationRequestDTO> dtos) {
        for (CreateInstallationRequestDTO dto : dtos) {
            try {
                // Remove existence check from API as bulk import does its own checks
                // and might bypass constraints temporarily for loading
                create(dto);
            } catch (Exception e) {
                // Skip if duplicate or error
            }
        }
    }

    @Override
    public ApplicationKpisDTO getKpis() {
        long pendingApplications = repository.countByStatusAndDeletedFalse(InstallationRequestStatus.PENDING);

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long applicationsChangeThisMonth = repository.countByDeletedFalseAndCreatedAtAfter(startOfMonth);

        long approvedApplications = repository.countByStatusAndDeletedFalse(InstallationRequestStatus.APPROVED);
        long installedToday = repository.countByStatusAndDeletedFalseAndInstallationDate(InstallationRequestStatus.INSTALLED, LocalDate.now());

        long rejectedApplications = repository.countByStatusAndDeletedFalse(InstallationRequestStatus.REJECTED);
        long rejectedApplicationsChangeThisMonth = repository.countByStatusAndDeletedFalseAndRejectedDateAfter(InstallationRequestStatus.REJECTED, startOfMonth.toLocalDate());

        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        BigDecimal projectedRevenue = repository.sumProjectedRevenueAfter(last30Days);

        return ApplicationKpisDTO.builder()
                .pendingApplications(pendingApplications)
                .applicationsChangeThisMonth(applicationsChangeThisMonth)
                .approvedApplications(approvedApplications)
                .installedToday(installedToday)
                .rejectedApplications(rejectedApplications)
                .rejectedApplicationsChangeThisMonth(rejectedApplicationsChangeThisMonth)
                .projectedRevenue(projectedRevenue)
                .build();
    }

    private void registerOperation(
            Supply supply,
            SupplyOperationType type,
            String reason,
            String observations
    ) {

        SupplyOperation operation = new SupplyOperation();

        operation.setSupply(supply);
        operation.setOperationType(type);
        operation.setOperationDate(LocalDate.now());
        operation.setReason(reason);
        operation.setPerformedBy(authUtils.getCurrentUser().getNames() + " " + authUtils.getCurrentUser().getLastNames());
        operation.setObservations(observations);

        operationRepository.save(operation);
    }

}