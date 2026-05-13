package com.epsel.epsel_api.modules.properties.dto;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePropertyDTO {

    @NotNull(message = "Cliente es requerido")
    private UUID customerId;

    @NotNull(message = "Tipo de propiedad es requerido")
    private PropertyType type;

    @NotBlank(message = "Dirección es requerida")
    private String address;

    private String reference;

    @NotNull(message = "Zona es requerida")
    private UUID zoneId;
}