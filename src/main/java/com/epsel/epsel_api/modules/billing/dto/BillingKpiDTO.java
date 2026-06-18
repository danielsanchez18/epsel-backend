package com.epsel.epsel_api.modules.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKpiDTO {
    private long pendingCount;
    private long overdueCount;
    private long paidCount;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
}
