package com.epsel.epsel_api.modules.supplies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallSupplyDTO {

    @NotBlank(message = "El número de medidor es requerido")
    private String meterNumber;

}