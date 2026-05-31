package com.epsel.epsel_api.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DashboardChartDTO {

    private String label;

    private BigDecimal value;

}
