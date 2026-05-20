package com.epsel.epsel_api.modules.configurations.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateServiceFeeConfigurationDTO {

    @DecimalMin(
            value = "0.00",
            message = "El monto debe ser mayor o igual a 0"
    )
    private BigDecimal amount;
    private Boolean active;

}