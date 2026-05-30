package com.epsel.epsel_api.modules.supplyWorkOrder.service;

import com.epsel.epsel_api.modules.supplyWorkOrder.dto.request.*;
import com.epsel.epsel_api.modules.supplyWorkOrder.dto.response.SupplyWorkOrderResponseDTO;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupplyWorkOrderService {

    SupplyWorkOrderResponseDTO create(
            CreateSupplyWorkOrderDTO dto
    );

    SupplyWorkOrderResponseDTO getById(
            UUID id
    );

    Page<SupplyWorkOrderResponseDTO> search(
            UUID supplyId,
            WorkOrderType type,
            WorkOrderStatus status,
            Pageable pageable
    );

    SupplyWorkOrderResponseDTO assign(
            UUID id,
            AssignWorkOrderDTO dto
    );

    SupplyWorkOrderResponseDTO start(
            UUID id,
            StartWorkOrderDTO dto
    );

    SupplyWorkOrderResponseDTO complete(
            UUID id,
            CompleteWorkOrderDTO dto
    );

    SupplyWorkOrderResponseDTO cancel(
            UUID id,
            CancelWorkOrderDTO dto
    );

}