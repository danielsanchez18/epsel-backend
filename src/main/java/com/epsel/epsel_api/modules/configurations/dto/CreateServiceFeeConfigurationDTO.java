package com.epsel.epsel_api.modules.configurations.dto;

import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateServiceFeeConfigurationDTO {

    @NotNull(message = "La zona de servicio es obligatoria")
    private UUID zoneId;

    @NotNull(message = "El tipo de tarifa es obligatorio")
    private ServiceFeeType feeType;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(
            value = "0.00",
            message = "El monto debe ser mayor o igual a 0"
    )
    private BigDecimal amount;
}