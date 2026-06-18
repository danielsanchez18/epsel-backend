package com.epsel.epsel_api.modules.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentKpiDTO {
    private BigDecimal totalToday;
    private BigDecimal totalPeriod;
    private BigDecimal totalCash;
    private BigDecimal totalYape;
    private BigDecimal totalTransfer;
}
