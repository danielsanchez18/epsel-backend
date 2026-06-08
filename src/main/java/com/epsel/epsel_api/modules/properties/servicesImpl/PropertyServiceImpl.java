package com.epsel.epsel_api.modules.properties.servicesImpl;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.dto.CreatePropertyDTO;
import com.epsel.epsel_api.modules.properties.dto.PropertyResponseDTO;
import com.epsel.epsel_api.modules.properties.dto.UpdatePropertyDTO;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.properties.services.PropertyService;
import com.epsel.epsel_api.modules.properties.specifications.PropertySpecification;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import com.epsel.epsel_api.modules.properties.dto.PropertyKpisDTO;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository repository;
    private final CustomerRepository customerRepository;
    private final ServiceZoneRepository zoneRepository;
    private final SupplyRepository supplyRepository;
    private final SupplyWorkOrderRepository supplyWorkOrderRepository;

    @Override
    public PropertyResponseDTO create(CreatePropertyDTO dto) {

        Customer customer = customerRepository.findByIdAndDeletedFalse(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        ServiceZone zone = zoneRepository.findByIdAndDeletedFalse(dto.getZoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        validateCoordinates(
                dto.getLatitude(),
                dto.getLongitude()
        );

        validateCadastralCode(
                dto.getCadastralCode(),
                null
        );

        Property property = new Property();

        property.setCustomer(customer);

        property.setType(dto.getType());

        property.setCadastralCode(
                dto.getCadastralCode()
        );

        property.setLatitude(
                dto.getLatitude()
        );

        property.setLongitude(
                dto.getLongitude()
        );

        property.setAddress(
                dto.getAddress()
        );

        property.setReference(
                dto.getReference()
        );

        property.setZone(zone);

        Property saved =
                repository.save(property);

        return mapResponse(saved);
    }

    @Override
    public PropertyResponseDTO update(
            UUID id,
            UpdatePropertyDTO dto
    ) {

        Property property = repository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Propiedad no encontrada"
                        )
                );

        validateCoordinates(
                dto.getLatitude(),
                dto.getLongitude()
        );

        validateCadastralCode(
                dto.getCadastralCode(),
                property.getId()
        );

        if (dto.getType() != null) {

            property.setType(dto.getType());
        }

        if (dto.getAddress() != null) {

            property.setAddress(dto.getAddress());
        }

        if (dto.getReference() != null) {

            property.setReference(dto.getReference());
        }

        if (dto.getCadastralCode() != null) {

            property.setCadastralCode(
                    dto.getCadastralCode()
            );
        }

        if (dto.getLatitude() != null) {

            property.setLatitude(
                    dto.getLatitude()
            );
        }

        if (dto.getLongitude() != null) {

            property.setLongitude(
                    dto.getLongitude()
            );
        }

        if (dto.getZoneId() != null) {

            ServiceZone zone = zoneRepository
                    .findByIdAndDeletedFalse(dto.getZoneId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Zona no encontrada"
                            )
                    );

            property.setZone(zone);
        }

        Property saved =
                repository.save(property);

        return mapResponse(saved);
    }

    @Override
    public PropertyResponseDTO getById(UUID id) {

        Property property = repository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Propiedad no encontrada"
                        )
                );

        return mapResponse(property);
    }

    @Override
    public Page<PropertyResponseDTO> search(
            String search,
            PropertyType type,
            UUID customerId,
            Pageable pageable
    ) {

        return repository.findAll(
                PropertySpecification.search(
                        search,
                        type,
                        customerId
                ),
                pageable
        ).map(this::mapResponse);
    }

    @Override
    public void delete(UUID id) {

        Property property = repository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Propiedad no encontrada"
                        )
                );

        Boolean hasSupply =
                supplyRepository
                        .existsByPropertyAndDeletedFalse(
                                property
                        );

        if (Boolean.TRUE.equals(hasSupply)) {

            throw new BadRequestException(
                    "No se puede eliminar un predio con suministro asociado"
            );
        }

        property.setDeleted(true);

        repository.save(property);
    }

    private void validateCoordinates(
            Double latitude,
            Double longitude
    ) {

        if (
                latitude != null &&
                        (latitude < -90 || latitude > 90)
        ) {

            throw new BadRequestException(
                    "Latitud inválida"
            );
        }

        if (
                longitude != null &&
                        (longitude < -180 || longitude > 180)
        ) {

            throw new BadRequestException(
                    "Longitud inválida"
            );
        }
    }

    private void validateCadastralCode(
            String cadastralCode,
            UUID currentPropertyId
    ) {

        if (
                cadastralCode == null ||
                        cadastralCode.isBlank()
        ) {

            return;
        }

        repository.findByCadastralCode(cadastralCode)
                .ifPresent(existing -> {

                    if (
                            currentPropertyId == null ||
                                    !existing.getId().equals(currentPropertyId)
                    ) {

                        throw new BadRequestException(
                                "El código catastral ya está registrado"
                        );
                    }
                });
    }

    private PropertyResponseDTO mapResponse(
            Property property
    ) {

        return PropertyResponseDTO.builder()

                .id(property.getId())

                .customerId(
                        property.getCustomer().getId()
                )

                .customerName(
                        property.getCustomer().getFullName()
                )

                .type(property.getType())

                .cadastralCode(
                        property.getCadastralCode()
                )

                .address(
                        property.getAddress()
                )

                .latitude(
                        property.getLatitude()
                )

                .longitude(
                        property.getLongitude()
                )

                .reference(
                        property.getReference()
                )

                .zoneId(
                        property.getZone().getId()
                )

                .zoneName(
                        property.getZone().getName()
                )
                .build();
    }

    @Override
    public PropertyKpisDTO getKpis() {
        long totalProperties = repository.countByDeletedFalse();

        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        long propertiesChangeThisMonth = repository.countCreatedInMonth(month, year);

        long activeProperties = repository.countActiveProperties();
        double activePropertiesPercentage = totalProperties > 0 
                ? (double) activeProperties * 100.0 / totalProperties 
                : 0.0;

        long propertiesWithoutSupply = repository.countPropertiesWithoutSupply();

        long pendingReconnections = supplyWorkOrderRepository.countByTypeAndStatusInAndDeletedFalse(
                WorkOrderType.RECONNECTION,
                Arrays.asList(WorkOrderStatus.PENDING, WorkOrderStatus.ASSIGNED, WorkOrderStatus.IN_PROGRESS)
        );

        long criticalDebtProperties = repository.countCriticalDebtProperties();
        long propertiesWithHighDebtCount = repository.countCriticalDebtPropertiesOver1000();

        return PropertyKpisDTO.builder()
                .totalProperties(totalProperties)
                .propertiesChangeThisMonth(propertiesChangeThisMonth)
                .activeProperties(activeProperties)
                .activePropertiesPercentage(activePropertiesPercentage)
                .propertiesWithoutSupply(propertiesWithoutSupply)
                .pendingReconnections(pendingReconnections)
                .criticalDebtProperties(criticalDebtProperties)
                .propertiesWithHighDebtCount(propertiesWithHighDebtCount)
                .build();
    }

    @Override
    public com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreatePropertyDTO> previewImport(org.springframework.web.multipart.MultipartFile file) {
        if (!com.epsel.epsel_api.shared.utils.ExcelImportHelper.hasExcelFormat(file)) {
            throw new BadRequestException("Formato de archivo inválido. Por favor suba un archivo Excel o CSV.");
        }

        java.util.List<java.util.List<String>> rows = com.epsel.epsel_api.shared.utils.ExcelImportHelper.readExcel(file);
        
        java.util.List<CreatePropertyDTO> validData = new java.util.ArrayList<>();
        java.util.List<com.epsel.epsel_api.shared.responses.ImportErrorDTO> errors = new java.util.ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            java.util.List<String> row = rows.get(i);
            int rowNum = i + 2; 
            java.util.List<String> rowErrors = new java.util.ArrayList<>();
            
            // Expected columns: DNI/RUC Cliente, Tipo (RESIDENTIAL/COMMERCIAL/etc), Código Catastral, Dirección, Referencia, Nombre de Zona
            if (row.size() < 6) {
                rowErrors.add("La fila no contiene todas las columnas requeridas (Documento Cliente, Tipo Predio, Cod. Catastral, Dirección, Referencia, Zona)");
                errors.add(new com.epsel.epsel_api.shared.responses.ImportErrorDTO(rowNum, rowErrors));
                continue;
            }

            String docCustomer = row.get(0);
            String typeStr = row.get(1);
            String cadastralCode = row.get(2);
            String address = row.get(3);
            String reference = row.get(4);
            String zoneName = row.get(5);

            UUID customerId = null;
            if (docCustomer == null || docCustomer.isEmpty()) {
                rowErrors.add("El documento del cliente es requerido");
            } else {
                java.util.Optional<Customer> optCust = customerRepository.findByDocumentNumberAndDeletedFalse(docCustomer);
                if (optCust.isEmpty()) {
                    rowErrors.add("No se encontró ningún cliente registrado con el documento: " + docCustomer);
                } else {
                    customerId = optCust.get().getId();
                }
            }

            PropertyType type = null;
            if (typeStr != null && (typeStr.equalsIgnoreCase("HOUSE") || typeStr.equalsIgnoreCase("CASA") || typeStr.equalsIgnoreCase("RESIDENCIAL"))) {
                type = PropertyType.HOUSE;
            } else if (typeStr != null && (typeStr.equalsIgnoreCase("BUSINESS") || typeStr.equalsIgnoreCase("COMERCIAL") || typeStr.equalsIgnoreCase("NEGOCIO"))) {
                type = PropertyType.BUSINESS;
            } else if (typeStr != null && (typeStr.equalsIgnoreCase("INDUSTRIAL") || typeStr.equalsIgnoreCase("INDUSTRIAL"))) {
                type = PropertyType.INDUSTRIAL;
            } else {
                rowErrors.add("Tipo de predio inválido (CASA o RESIDENCIAL/NEGOCIO o COMERCIAL/INDUSTRIAL)");
            }

            if (cadastralCode == null || cadastralCode.isEmpty()) {
                rowErrors.add("El código catastral es requerido");
            } else if (repository.findByCadastralCode(cadastralCode).isPresent()) {
                rowErrors.add("El código catastral ya se encuentra registrado en otro predio");
            }

            if (address == null || address.isEmpty()) {
                rowErrors.add("La dirección es requerida");
            }

            UUID zoneId = null;
            if (zoneName == null || zoneName.isEmpty()) {
                rowErrors.add("El nombre de la zona es requerido");
            } else {
                java.util.Optional<ServiceZone> optZone = zoneRepository.findByNameIgnoreCase(zoneName);
                if (optZone.isEmpty()) {
                    rowErrors.add("No se encontró ninguna zona con el nombre: " + zoneName);
                } else {
                    zoneId = optZone.get().getId();
                }
            }

            if (rowErrors.isEmpty()) {
                CreatePropertyDTO dto = new CreatePropertyDTO();
                dto.setCustomerId(customerId);
                dto.setType(type);
                dto.setCadastralCode(cadastralCode);
                dto.setAddress(address);
                dto.setReference(reference);
                dto.setZoneId(zoneId);
                validData.add(dto);
            } else {
                errors.add(new com.epsel.epsel_api.shared.responses.ImportErrorDTO(rowNum, rowErrors));
            }
        }

        return com.epsel.epsel_api.shared.responses.ImportPreviewResponse.<CreatePropertyDTO>builder()
                .totalRows(rows.size())
                .validCount(validData.size())
                .invalidCount(errors.size())
                .validData(validData)
                .errors(errors)
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void createBulk(java.util.List<CreatePropertyDTO> dtos) {
        for (CreatePropertyDTO dto : dtos) {
            if (repository.findByCadastralCode(dto.getCadastralCode()).isPresent()) {
                continue; 
            }
            create(dto);
        }
    }
}

