package com.epsel.epsel_api.modules.collections.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionKpiDTO {
    private long pendingCount;
    private long overdueCount;
    private BigDecimal totalPendingAmount;
    private BigDecimal totalOverdueAmount;
    private long delinquentCustomersCount;
    private long suppliesToCutCount;
}
