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
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstallationRequestServiceImpl implements InstallationRequestService {

    private final InstallationRequestRepository repository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final ServiceFeeConfigurationRepository feeRepository;
    private final SupplyRepository supplyRepository;
    private final AuthUtils authUtils;

    @Override
    public InstallationRequestResponseDTO create(CreateInstallationRequestDTO dto) {

        Customer customer = customerRepository.findByIdAndDeletedFalse(dto.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Property property = propertyRepository.findByIdAndDeletedFalse(dto.getPropertyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        Boolean exists = repository
                .existsByPropertyAndInternalReferenceIgnoreCaseAndStatusIn(
                        property,
                        dto.getInternalReference(),
                        List.of(
                                InstallationRequestStatus.PENDING,
                                InstallationRequestStatus.APPROVED
                        )
        );

        if (Boolean.TRUE.equals(exists)) {
            throw new BadRequestException("Ya existe una solicitud activa para esta propiedad");
        }

        ServiceFeeConfiguration fee = feeRepository
                        .findByZone_IdAndFeeTypeAndActiveTrue(
                                property.getZone().getId(),
                                ServiceFeeType.INSTALLATION
                        )
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no disponible esta zona"));

        InstallationRequest request = new InstallationRequest();

        request.setCustomer(customer);
        request.setProperty(property);
        request.setInternalReference(dto.getInternalReference());
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

}