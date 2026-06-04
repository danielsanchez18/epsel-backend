package com.epsel.epsel_api.modules.supplies.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplyKpisDTO {
    private long totalSupplies;
    private long suppliesChangeThisMonth;
    private long activeSupplies;
    private double activeSuppliesPercentage;
    private long suspendedSupplies;
    private long suspendedSuppliesChangeThisMonth;
    private long pendingReconnections;
    private long reconnectionsThisMonth;
}
