package com.epsel.epsel_api.modules.supplies.servicesImpl;

import com.epsel.epsel_api.modules.auth.utils.AuthUtils;
import com.epsel.epsel_api.modules.supplies.dto.ReconnectSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyDetailsDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyResponseDTO;
import com.epsel.epsel_api.modules.supplies.dto.SuspendSupplyDTO;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplies.services.SupplyService;
import com.epsel.epsel_api.modules.supplies.specifications.SupplySpecification;
import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.repository.SupplyOperationRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import com.epsel.epsel_api.modules.supplies.dto.SupplyKpisDTO;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.dto.CreateSupplyBulkDTO;
import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
import com.epsel.epsel_api.shared.responses.ImportErrorDTO;
import com.epsel.epsel_api.shared.responses.ImportPreviewResponse;
import com.epsel.epsel_api.shared.utils.ExcelImportHelper;

@Service
@RequiredArgsConstructor
public class SupplyServiceImpl implements SupplyService {

    private final SupplyRepository repository;
    private final SupplyOperationRepository operationRepository;
    private final AuthUtils authUtils;
    private final SupplyWorkOrderRepository supplyWorkOrderRepository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final InstallationRequestRepository installationRequestRepository;

    @Override
    public Page<SupplyResponseDTO> findAll(
            String search,
            SupplyStatus status,
            UUID zoneId,
            Pageable pageable
    ) {

        return repository.findAll(
                SupplySpecification.search(
                        search,
                        status,
                        zoneId
                ),
                pageable
        ).map(this::mapResponse);
    }

    @Override
    public SupplyDetailsDTO getById(UUID id) {

        Supply supply = repository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Suministro no encontrado"
                        )
                );

        return mapDetails(supply);
    }

    @Override
    public Page<SupplyDetailsDTO> getByCustomerId(UUID customerId, Pageable pageable) {
        return repository.findByCustomerIdAndDeletedFalse(customerId, pageable)
                .map(this::mapDetails);
    }

    @Override
    public Page<SupplyDetailsDTO> getByPropertyId(UUID propertyId, Pageable pageable) {
        return repository.findByPropertyIdAndDeletedFalse(propertyId, pageable)
                .map(this::mapDetails);
    }

    @Override
    public SupplyDetailsDTO getByInstallationRequestId(UUID installationRequestId) {

        Supply supply = repository.
                findByInstallationRequestIdAndDeletedFalse(installationRequestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Suministro no encontrado para la solicitud de instalación"
                        )
                );

        return mapDetails(supply);
    }

    @Override
    public SupplyResponseDTO suspend(UUID id, SuspendSupplyDTO dto) {

        Supply supply = getSupply(id);

        if (supply.getStatus() != SupplyStatus.ACTIVE) {
            throw new BadRequestException("Solo se pueden suspender suministros activos");
        }

        supply.setStatus(SupplyStatus.SUSPENDED);
        supply.setConnected(false);

        Supply saved = repository.save(supply);

        registerOperation(
                saved,
                SupplyOperationType.SUSPENSION,
                dto.getReason(),
                dto.getReason()
        );

        return mapResponse(saved);
    }

    @Override
    public SupplyResponseDTO reconnect(UUID id, ReconnectSupplyDTO dto) {

        Supply supply = getSupply(id);

        if (supply.getStatus() != SupplyStatus.CUT_OFF && supply.getStatus() != SupplyStatus.SUSPENDED) {
            throw new BadRequestException("Solo se pueden reconectar suministros cortados o suspendidos");
        }

        supply.setStatus(SupplyStatus.ACTIVE);
        supply.setConnected(true);
        supply.setReconnectionDate(LocalDate.now());

        Supply saved = repository.save(supply);

        registerOperation(
                saved,
                SupplyOperationType.RECONNECTION,
                dto.getReason(),
                dto.getReason()
        );

        return mapResponse(saved);
    }

    @Override
    public SupplyResponseDTO cutOff(UUID id, SuspendSupplyDTO dto) {

        Supply supply = getSupply(id);

        if (supply.getStatus() != SupplyStatus.SUSPENDED) {
            throw new BadRequestException("Solo se pueden cortar suministros suspendidos");
        }

        supply.setStatus(SupplyStatus.CUT_OFF);
        supply.setConnected(false);
        supply.setCutOffDate(LocalDate.now());
        supply.setCutOffReason(dto.getReason());

        Supply saved = repository.save(supply);

        registerOperation(
                saved,
                SupplyOperationType.CUT_OFF,
                dto.getReason(),
                dto.getReason()
        );

        return mapResponse(saved);
    }

    private Supply getSupply(UUID id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suministro no encontrado"));
    }

    private SupplyResponseDTO mapResponse(Supply supply) {

        return SupplyResponseDTO.builder()
                .id(supply.getId())
                .supplyNumber(supply.getSupplyNumber())
                .meterNumber(supply.getMeterNumber())
                .internalReference(supply.getInternalReference())
                .status(supply.getStatus())
                .customerName(supply.getCustomer().getFullName())
                .customerDocument(supply.getCustomer().getDocumentNumber())
                .propertyId(supply.getProperty().getId())
                .propertyAddress(supply.getProperty().getAddress())
                .zoneName(supply.getProperty().getZone().getName())
                .supplyType(supply.getSupplyType())
                .lastReading(supply.getLastReading())
                .installationDate(supply.getInstallationDate())
                .activationDate(supply.getActivationDate())
                .build();
    }

    private SupplyDetailsDTO mapDetails(Supply supply) {

        return SupplyDetailsDTO.builder()
                .id(supply.getId())
                .supplyNumber(supply.getSupplyNumber())
                .meterNumber(supply.getMeterNumber())
                .status(supply.getStatus())
                .customerName(supply.getCustomer().getFullName())
                .customerDocument(supply.getCustomer().getDocumentNumber())
                .customerPhone(supply.getCustomer().getPhone())
                .propertyAddress(supply.getProperty().getAddress())
                .propertyReference(supply.getProperty().getReference())
                .cadastralCode(supply.getProperty().getCadastralCode())
                .zoneName(supply.getProperty().getZone().getName())
                .supplyType(supply.getSupplyType())
                .lastReading(supply.getLastReading())
                .latitude(supply.getProperty().getLatitude())
                .longitude(supply.getProperty().getLongitude())
                .installationDate(supply.getInstallationDate())
                .activationDate(supply.getActivationDate())
                .cutOffDate(supply.getCutOffDate())
                .reconnectionDate(supply.getReconnectionDate())
                .cutOffReason(supply.getCutOffReason())
                .createdAt(supply.getCreatedAt().toLocalDate())
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

    @Override
    public ImportPreviewResponse<CreateSupplyBulkDTO> previewImport(MultipartFile file) {
        if (!ExcelImportHelper.hasExcelFormat(file)) {
            throw new BadRequestException("Formato de archivo inválido. Por favor suba un archivo Excel o CSV.");
        }

        List<List<String>> rows = ExcelImportHelper.readExcel(file);

        List<CreateSupplyBulkDTO> validData = new ArrayList<>();
        List<ImportErrorDTO> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            int rowNumber = i + 2;
            
            List<String> rowErrors = new ArrayList<>();

            if (row.size() < 3) {
                rowErrors.add("La fila no contiene las columnas mínimas requeridas (Documento Cliente, Código Catastral, Número de Medidor)");
                errors.add(new ImportErrorDTO(rowNumber, rowErrors));
                continue;
            }

            String documentNumber = row.get(0);
            String cadastralCode = row.get(1);
            String meterNumber = row.get(2);
            String internalReference = row.size() > 3 ? row.get(3) : "";
            String installationDateStr = row.size() > 4 ? row.get(4) : "";
            String lastReadingStr = row.size() > 5 ? row.get(5) : "";

            if (documentNumber == null || documentNumber.trim().isEmpty()) {
                rowErrors.add("Documento Cliente es requerido");
            }
            if (cadastralCode == null || cadastralCode.trim().isEmpty()) {
                rowErrors.add("Código Catastral es requerido");
            }
            if (meterNumber == null || meterNumber.trim().isEmpty()) {
                rowErrors.add("Número de Medidor es requerido");
            } else if (repository.existsByMeterNumberAndDeletedFalse(meterNumber.trim())) {
                rowErrors.add("Número de Medidor ya existe");
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
            
            LocalDate installationDate = LocalDate.now();
            if (installationDateStr != null && !installationDateStr.trim().isEmpty()) {
                try {
                    installationDate = LocalDate.parse(installationDateStr.trim());
                } catch (Exception e) {
                    rowErrors.add("Fecha inválida. Use YYYY-MM-DD");
                }
            }
            
            Integer lastReading = 0;
            if (lastReadingStr != null && !lastReadingStr.trim().isEmpty()) {
                try {
                    lastReading = Integer.parseInt(lastReadingStr.trim());
                } catch (Exception e) {
                    rowErrors.add("Última Lectura debe ser un número entero");
                }
            }

            if (!rowErrors.isEmpty()) {
                errors.add(new ImportErrorDTO(rowNumber, rowErrors));
            } else {
                CreateSupplyBulkDTO dto = new CreateSupplyBulkDTO();
                dto.setCustomerId(customer.getId());
                dto.setPropertyId(property.getId());
                dto.setMeterNumber(meterNumber.trim());
                dto.setInternalReference(internalReference != null ? internalReference.trim() : "");
                dto.setInstallationDate(installationDate);
                dto.setLastReading(lastReading);
                validData.add(dto);
            }
        }
        
        return ImportPreviewResponse.<CreateSupplyBulkDTO>builder()
                .totalRows(rows.size())
                .validCount(validData.size())
                .invalidCount(errors.size())
                .validData(validData)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public void createBulk(List<CreateSupplyBulkDTO> dtos) {
        for (CreateSupplyBulkDTO dto : dtos) {
            try {
                if (repository.existsByMeterNumberAndDeletedFalse(dto.getMeterNumber())) continue;
                
                Customer customer = customerRepository.findByIdAndDeletedFalse(dto.getCustomerId()).orElse(null);
                Property property = propertyRepository.findByIdAndDeletedFalse(dto.getPropertyId()).orElse(null);

                if (customer == null || property == null) continue;

                InstallationRequest req = new InstallationRequest();
                req.setCustomer(customer);
                req.setProperty(property);
                req.setInternalReference(dto.getInternalReference());
                req.setInstallationCost(BigDecimal.ZERO);
                req.setStatus(InstallationRequestStatus.INSTALLED);
                req.setRequestedDate(dto.getInstallationDate());
                req.setApprovedDate(dto.getInstallationDate());
                req.setInstallationDate(dto.getInstallationDate());
                req.setObservations("Generado por importación masiva de suministros");
                InstallationRequest savedReq = installationRequestRepository.save(req);

                Supply supply = new Supply();
                supply.setProperty(property);
                supply.setCustomer(customer);
                supply.setInstallationRequest(savedReq);
                supply.setStatus(SupplyStatus.ACTIVE);
                supply.setConnected(true);
                supply.setSupplyType(property.getType());
                
                long count = repository.count() + 1;
                supply.setSupplyNumber(String.format("SUM-%08d", count));
                
                supply.setInternalReference(dto.getInternalReference());
                supply.setInstallationDate(dto.getInstallationDate());
                supply.setActivationDate(dto.getInstallationDate());
                supply.setLastReading(dto.getLastReading());
                supply.setMeterNumber(dto.getMeterNumber());
                
                repository.save(supply);
            } catch (Exception e) {
                // Skip if duplicate or error
            }
        }
    }

    @Override
    public SupplyKpisDTO getKpis() {
        long totalSupplies = repository.countByDeletedFalse();

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long suppliesChangeThisMonth = repository.countByDeletedFalseAndCreatedAtAfter(startOfMonth);

        long activeSupplies = repository.countByStatusAndDeletedFalse(SupplyStatus.ACTIVE);
        double activeSuppliesPercentage = totalSupplies > 0 
                ? (double) activeSupplies * 100.0 / totalSupplies 
                : 0.0;

        long suspendedSupplies = repository.countByStatusAndDeletedFalse(SupplyStatus.SUSPENDED);
        long suspendedSuppliesChangeThisMonth = repository.countByStatusAndDeletedFalseAndCreatedAtAfter(SupplyStatus.SUSPENDED, startOfMonth);

        long pendingReconnections = supplyWorkOrderRepository.countByTypeAndStatusInAndDeletedFalse(
                WorkOrderType.RECONNECTION,
                Arrays.asList(WorkOrderStatus.PENDING, WorkOrderStatus.ASSIGNED, WorkOrderStatus.IN_PROGRESS)
        );

        long reconnectionsThisMonth = supplyWorkOrderRepository.countByTypeAndStatusAndDeletedFalseAndUpdatedAtAfter(
                WorkOrderType.RECONNECTION,
                WorkOrderStatus.COMPLETED,
                startOfMonth
        );

        return SupplyKpisDTO.builder()
                .totalSupplies(totalSupplies)
                .suppliesChangeThisMonth(suppliesChangeThisMonth)
                .activeSupplies(activeSupplies)
                .activeSuppliesPercentage(activeSuppliesPercentage)
                .suspendedSupplies(suspendedSupplies)
                .suspendedSuppliesChangeThisMonth(suspendedSuppliesChangeThisMonth)
                .pendingReconnections(pendingReconnections)
                .reconnectionsThisMonth(reconnectionsThisMonth)
                .build();
    }
}