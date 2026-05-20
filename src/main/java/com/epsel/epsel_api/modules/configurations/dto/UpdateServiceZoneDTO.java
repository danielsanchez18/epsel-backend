package com.epsel.epsel_api.modules.configurations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateServiceZoneDTO {

    @NotBlank(message = "El nombre de la zona de servicio es obligatorio")
    private String name;

    @NotBlank(message = "La descripción de la zona de servicio es obligatoria")
    private String description;

    private Boolean active;
}