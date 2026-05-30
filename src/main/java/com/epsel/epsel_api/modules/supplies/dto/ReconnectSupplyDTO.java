package com.epsel.epsel_api.modules.supplies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReconnectSupplyDTO {

    @NotBlank(message = "La razón es requerida")
    private String reason;

    private String observations;
}
