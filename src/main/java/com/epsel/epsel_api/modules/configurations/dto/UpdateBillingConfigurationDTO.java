package com.epsel.epsel_api.modules.configurations.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateBillingConfigurationDTO {

    @NotNull(message = "Los meses antes del recorte son obligatorios")
    @Min(
            value = 1,
            message = "Los meses antes del recorte deben ser mayores o iguales a 1"
    )
    private Integer monthsBeforeCut;

    @NotNull(message = "El porcentaje de interés de demora es obligatorio")
    @DecimalMin(
            value = "0.00",
            message = "El porcentaje de interés de demora debe ser mayor o igual a 0.00"
    )
    private BigDecimal lateInterestPercentage;

    @NotNull(message = "Los días de plazo son obligatorios")
    @Min(
            value = 0,
            message = "Los días de plazo deben ser mayores o iguales a 0"
    )
    private Integer graceDays;
}