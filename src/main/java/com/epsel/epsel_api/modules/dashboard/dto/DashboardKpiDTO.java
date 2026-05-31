package com.epsel.epsel_api.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardKpiDTO {

    private KpiMetricDTO totalCustomers;

    private KpiMetricDTO totalProperties;

    private KpiMetricDTO activeSupplies;

    private KpiMetricDTO suspendedSupplies;

    private KpiMetricDTO cutOffSupplies;

    private KpiMetricDTO pendingBillings;

    private KpiMetricDTO overdueBillings;

    private KpiMetricDTO totalBilledMonth;

    private KpiMetricDTO totalCollectedMonth;

    private KpiMetricDTO totalPendingCollection;
}