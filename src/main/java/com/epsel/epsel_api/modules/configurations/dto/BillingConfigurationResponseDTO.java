package com.epsel.epsel_api.modules.configurations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BillingConfigurationResponseDTO {

    private UUID id;
    private Integer monthsBeforeCut;
    private BigDecimal lateInterestPercentage;
    private Integer graceDays;
    private Boolean active;

}