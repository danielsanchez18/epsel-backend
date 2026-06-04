package com.epsel.epsel_api.modules.properties.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PropertyKpisDTO {
    private long totalProperties;
    private long propertiesChangeThisMonth;
    private long activeProperties;
    private double activePropertiesPercentage;
    private long propertiesWithoutSupply;
    private long pendingReconnections;
    private long criticalDebtProperties;
    private long propertiesWithHighDebtCount;
}
