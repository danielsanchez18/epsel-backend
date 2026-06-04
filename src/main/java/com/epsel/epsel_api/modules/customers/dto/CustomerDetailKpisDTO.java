package com.epsel.epsel_api.modules.customers.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class CustomerDetailKpisDTO {
    private BigDecimal totalDebt;
    private long overdueBillsCount;
    private double averageConsumption;
    private double consumptionChangePercentage;
    private LocalDate lastPaymentDate;
    private boolean lastPaymentDelayed;
    private long activeIncidentsCount;
    private long recentIncidentsCount;
}
