package com.epsel.epsel_api.modules.supplies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;

@Setter
public class ReconnectSupplyDTO {

    @NotBlank(message = "La observación es requerida")
    private String observation;

}
