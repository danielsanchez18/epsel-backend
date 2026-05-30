package com.epsel.epsel_api.modules.supplyWorkOrder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteWorkOrderDTO {

    @NotBlank(message = "Las observaciones son requeridas")
    private String observations;

    private String meterNumber;

}