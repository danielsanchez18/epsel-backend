package com.epsel.epsel_api.modules.supplyWorkOrder.serviceImpl;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.service.SupplyOperationService;
import com.epsel.epsel_api.modules.supplyWorkOrder.dto.request.*;
import com.epsel.epsel_api.modules.supplyWorkOrder.dto.response.SupplyWorkOrderResponseDTO;
import com.epsel.epsel_api.modules.supplyWorkOrder.entity.SupplyWorkOrder;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import com.epsel.epsel_api.modules.supplyWorkOrder.service.SupplyWorkOrderService;
import com.epsel.epsel_api.modules.supplyWorkOrder.specification.SupplyWorkOrderSpecification;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplyWorkOrderServiceImpl implements SupplyWorkOrderService {

    private final SupplyWorkOrderRepository repository;
    private final SupplyRepository supplyRepository;
    private final SupplyOperationService supplyOperationService;
    private final InstallationRequestRepository installationRequestRepository;

    @Override
    public SupplyWorkOrderResponseDTO create(CreateSupplyWorkOrderDTO dto) {

        Supply supply = supplyRepository.findById(dto.getSupplyId())
                .orElseThrow(() -> new ResourceNotFoundException("Suministro no encontrado"));

        if (supply.getDeleted()) {
            throw new ResourceNotFoundException("Suministro no encontrado");
        }

        boolean exists =
                repository
                        .existsBySupplyAndTypeAndStatusIn(
                                supply,
                                dto.getType(),
                                List.of(
                                        WorkOrderStatus.PENDING,
                                        WorkOrderStatus.ASSIGNED,
                                        WorkOrderStatus.IN_PROGRESS));

        if (exists) {
            throw new BadRequestException("Ya existe una orden activa de este tipo para el suministro");
        }

        SupplyWorkOrder workOrder = new SupplyWorkOrder();

        workOrder.setSupply(supply);
        workOrder.setType(dto.getType());
        workOrder.setStatus(WorkOrderStatus.PENDING);
        workOrder.setRequestedDate(LocalDate.now());
        workOrder.setScheduledDate(dto.getScheduledDate());
        workOrder.setReason(dto.getReason());
        workOrder.setObservations(dto.getObservations());

        SupplyWorkOrder saved = repository.save(workOrder);

        return mapResponse(saved);
    }

    @Override
    public SupplyWorkOrderResponseDTO getById(UUID id) {

        SupplyWorkOrder workOrder = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada"));

        if (workOrder.getDeleted()) {
            throw new ResourceNotFoundException("Orden de trabajo no encontrada");
        }

        return mapResponse(workOrder);
    }

    @Override
    public Page<SupplyWorkOrderResponseDTO> search(
            UUID supplyId,
            WorkOrderType type,
            WorkOrderStatus status,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            Pageable pageable) {

        return repository.findAll(SupplyWorkOrderSpecification.search(supplyId, type, status, startDate, endDate), pageable)
                .map(this::mapResponse);
    }

    @Override
    public SupplyWorkOrderResponseDTO assign(
            UUID id,
            AssignWorkOrderDTO dto
    ) {

        SupplyWorkOrder workOrder =
                getWorkOrder(id);

        if (
                workOrder.getStatus() !=
                        WorkOrderStatus.PENDING
        ) {

            throw new BadRequestException(
                    "Solo se pueden asignar órdenes pendientes"
            );
        }

        workOrder.setStatus(
                WorkOrderStatus.ASSIGNED
        );

        workOrder.setObservations(
                dto.getObservations()
        );

        return mapResponse(
                repository.save(workOrder)
        );
    }

    @Override
    public SupplyWorkOrderResponseDTO start(
            UUID id,
            StartWorkOrderDTO dto
    ) {

        SupplyWorkOrder workOrder =
                getWorkOrder(id);

        if (
                workOrder.getStatus() !=
                        WorkOrderStatus.ASSIGNED
        ) {

            throw new BadRequestException(
                    "Solo se pueden iniciar órdenes asignadas"
            );
        }

        workOrder.setStatus(
                WorkOrderStatus.IN_PROGRESS
        );

        workOrder.setObservations(
                dto.getObservations()
        );

        return mapResponse(
                repository.save(workOrder)
        );
    }

    @Override
    public SupplyWorkOrderResponseDTO complete(
            UUID id,
            CompleteWorkOrderDTO dto
    ) {

        SupplyWorkOrder workOrder =
                getWorkOrder(id);

        if (
                workOrder.getStatus() !=
                        WorkOrderStatus.IN_PROGRESS
        ) {

            throw new BadRequestException(
                    "La orden debe estar en progreso"
            );
        }

        Supply supply =
                workOrder.getSupply();

        switch (workOrder.getType()) {

            case INSTALLATION -> {

                if (dto.getMeterNumber() == null || dto.getMeterNumber().trim().isEmpty()) {
                    throw new BadRequestException("El número de medidor es requerido para completar la instalación");
                }

                if (supplyRepository.existsByMeterNumberAndDeletedFalse(dto.getMeterNumber().trim())) {
                    throw new BadRequestException("El número de medidor ya está registrado en el sistema");
                }

                supply.setMeterNumber(dto.getMeterNumber().trim());
                supply.setStatus(
                        SupplyStatus.ACTIVE
                );
                supply.setConnected(true);
                supply.setActivationDate(LocalDate.now());

                if (supply.getInstallationRequest() != null) {
                    var request = supply.getInstallationRequest();
                    request.setStatus(InstallationRequestStatus.INSTALLED);
                    request.setInstallationDate(LocalDate.now());
                    installationRequestRepository.save(request);
                }

                supplyRepository.save(supply);

                supplyOperationService
                        .registerOperation(
                                supply,
                                SupplyOperationType.INSTALLATION,
                                "Instalación completada",
                                workOrder.getObservations()
                        );
            }

            case SUSPENSION -> {

                supply.setStatus(
                        SupplyStatus.SUSPENDED
                );

                supplyOperationService
                        .registerOperation(
                                supply,
                                SupplyOperationType.SUSPENSION,
                                workOrder.getReason(),
                                workOrder.getReason()
                        );
            }

            case CUT_OFF -> {

                supply.setStatus(
                        SupplyStatus.CUT_OFF
                );

                supply.setCutOffDate(
                        LocalDate.now()
                );

                supply.setCutOffReason(
                        workOrder.getReason()
                );

                supplyOperationService
                        .registerOperation(
                                supply,
                                SupplyOperationType.CUT_OFF,
                                workOrder.getReason(),
                                workOrder.getObservations()
                        );
            }

            case RECONNECTION -> {

                supply.setStatus(
                        SupplyStatus.ACTIVE
                );

                supply.setReconnectionDate(
                        LocalDate.now()
                );

                supplyOperationService
                        .registerOperation(
                                supply,
                                SupplyOperationType.RECONNECTION,
                                "Reconexión realizada",
                                workOrder.getObservations()
                        );
            }

            default -> {
            }
        }

        supplyRepository.save(supply);

        workOrder.setStatus(
                WorkOrderStatus.COMPLETED
        );

        workOrder.setCompletedDate(
                LocalDate.now()
        );

        workOrder.setObservations(
                dto.getObservations()
        );

        return mapResponse(
                repository.save(workOrder)
        );
    }

    @Override
    public SupplyWorkOrderResponseDTO cancel(
            UUID id,
            CancelWorkOrderDTO dto
    ) {

        SupplyWorkOrder workOrder =
                getWorkOrder(id);

        if (
                workOrder.getStatus() ==
                        WorkOrderStatus.COMPLETED
        ) {

            throw new BadRequestException(
                    "No se puede cancelar una orden completada"
            );
        }

        workOrder.setStatus(
                WorkOrderStatus.CANCELLED
        );

        workOrder.setObservations(
                dto.getObservations()
        );

        return mapResponse(
                repository.save(workOrder)
        );
    }

    private SupplyWorkOrder getWorkOrder(
            UUID id
    ) {

        SupplyWorkOrder workOrder =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Orden de trabajo no encontrada"
                                ));

        if (workOrder.getDeleted()) {

            throw new ResourceNotFoundException(
                    "Orden de trabajo no encontrada"
            );
        }

        return workOrder;
    }

    private SupplyWorkOrderResponseDTO mapResponse(SupplyWorkOrder workOrder) {

        return SupplyWorkOrderResponseDTO.builder()
                .id(workOrder.getId())
                .supplyId(workOrder.getSupply().getId())
                .supplyNumber(workOrder.getSupply().getSupplyNumber())
                .type(workOrder.getType())
                .status(workOrder.getStatus())
                .requestedDate(workOrder.getRequestedDate())
                .scheduledDate(workOrder.getScheduledDate())
                .completedDate(workOrder.getCompletedDate())
                .reason(workOrder.getReason())
                .observations(workOrder.getObservations())
                .customerName(workOrder.getSupply().getCustomer().getFullName())
                .propertyAddress(workOrder.getSupply().getProperty().getAddress())
                .build();
    }
}
