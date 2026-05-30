package com.epsel.epsel_api.modules.supplyWorkOrder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelWorkOrderDTO {

    @NotBlank(message = "El motivo de cancelación es requerido")
    private String observations;

}