package com.epsel.epsel_api.modules.supplies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuspendSupplyDTO {

    @NotBlank(message = "El motivo es requerido")
    private String reason;

}