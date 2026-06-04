package com.epsel.epsel_api.modules.customers.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class CustomerKpisDTO {
    private long totalCustomers;
    private long customersChangeThisMonth;
    private long activeCustomers;
    private double activeCustomersPercentage;
    private long delinquentCustomers;
    private BigDecimal delinquentAmount;
    private long newCustomersLast30Days;
}
