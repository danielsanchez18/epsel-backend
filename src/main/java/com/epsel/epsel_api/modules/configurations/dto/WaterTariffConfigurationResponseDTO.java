package com.epsel.epsel_api.modules.configurations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class WaterTariffConfigurationResponseDTO {

    private UUID id;
    private UUID zoneId;
    private String zoneName;
    private BigDecimal pricePerM3;
    private BigDecimal fixedCharge;
    private BigDecimal taxPercentage;
    private LocalDate effectiveDate;
    private Boolean active;

}