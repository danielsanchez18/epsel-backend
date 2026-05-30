package com.epsel.epsel_api.modules.supplyWorkOrder.dto.request;

import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateSupplyWorkOrderDTO {

    @NotNull
    private UUID supplyId;

    @NotNull
    private WorkOrderType type;

    @NotBlank
    private String reason;

    private String observations;
    private LocalDate scheduledDate;

}