package com.epsel.epsel_api.modules.configurations.dto;

import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ServiceFeeConfigurationResponseDTO {

    private UUID id;
    private UUID zoneId;
    private String zoneName;
    private ServiceFeeType feeType;
    private BigDecimal amount;
    private Boolean active;

}