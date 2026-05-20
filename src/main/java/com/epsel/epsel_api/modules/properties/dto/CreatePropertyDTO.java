package com.epsel.epsel_api.modules.properties.dto;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Código catastral es requerido")
    @Size(max = 50, message = "El código catastral no puede exceder los 50 caracteres")
    private String cadastralCode;

    private Double latitude;
    private Double longitude;

    private String reference;

    @NotNull(message = "Zona es requerida")
    private UUID zoneId;
}