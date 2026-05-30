package com.epsel.epsel_api.modules.supplyWorkOrder.dto.response;

import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupplyWorkOrderResponseDTO {

    private UUID id;
    private UUID supplyId;
    private String supplyNumber;
    private WorkOrderType type;
    private WorkOrderStatus status;
    private LocalDate requestedDate;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String reason;
    private String observations;
    private String customerName;
    private String propertyAddress;

}