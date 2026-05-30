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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplyServiceImpl implements SupplyService {

    private final SupplyRepository repository;
    private final SupplyOperationRepository operationRepository;
    private final AuthUtils authUtils;

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
}