package com.epsel.epsel_api.modules.payments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelPaymentDTO {

    @NotBlank(message = "Motivo requerido")
    private String reason;

}