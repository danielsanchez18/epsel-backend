package com.epsel.epsel_api.modules.supplies.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateInstallationRequestDTO {

    @NotNull(message = "El cliente es requerido")
    private UUID customerId;

    @NotNull(message = "La propiedad es requerida")
    private UUID propertyId;

    private String internalReference;
    private LocalDate requestedDate;
    private String observations;

}
