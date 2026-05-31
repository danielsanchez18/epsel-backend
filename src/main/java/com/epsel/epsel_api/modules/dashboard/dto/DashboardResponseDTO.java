package com.epsel.epsel_api.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DashboardResponseDTO {

    private DashboardKpiDTO kpis;

    private List<DashboardAlertDTO> alerts;

    private List<DashboardChartDTO> billingChart;

    private List<DashboardChartDTO> paymentChart;

    private List<DashboardChartDTO> consumptionChart;

    private List<DashboardActivityDTO> recentActivities;

}