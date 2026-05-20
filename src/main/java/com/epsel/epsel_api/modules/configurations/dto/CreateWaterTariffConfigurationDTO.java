package com.epsel.epsel_api.modules.configurations.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateWaterTariffConfigurationDTO {

    @NotNull(message = "La zona de servicio es obligatoria")
    private UUID zoneId;

    @NotNull(message = "Precio por m3 es requerido")
    @DecimalMin(
            value = "0.00",
            message = "Precio por m3 debe ser mayor o igual a 0"
    )
    private BigDecimal pricePerM3;

    @NotNull(message = "Cargo fijo es requerido")
    @DecimalMin(
            value = "0.00",
            message = "Cargo fijo debe ser mayor o igual a 0"
    )
    private BigDecimal fixedCharge;

    @NotNull(message = "Porcentaje de impuesto es requerido")
    @DecimalMin(
            value = "0.00",
            message = "Porcentaje de impuesto debe ser mayor o igual a 0"
    )
    @Max(
            value = 100,
            message = "Porcentaje de impuesto debe ser menor o igual a 100"
    )
    private BigDecimal taxPercentage;

    @NotNull(message = "La fecha de vigencia es obligatoria")
    private LocalDate effectiveDate;
}