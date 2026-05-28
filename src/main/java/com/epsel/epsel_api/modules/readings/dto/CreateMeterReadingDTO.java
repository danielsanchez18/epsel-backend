package com.epsel.epsel_api.modules.readings.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateMeterReadingDTO {

    @NotNull(message = "Suministro es requerido")
    private UUID supplyId;

    @NotNull(message = "Lectura actual es requerida")
    @Min(value = 0, message = "La lectura actual debe ser un número positivo")
    private Integer currentReading;

    @NotNull(message = "Fecha de lectura es requerida")
    private LocalDate readingDate;

    private String meterPhotoUrl;
    private String ocrValue;
    private String observations;

}
