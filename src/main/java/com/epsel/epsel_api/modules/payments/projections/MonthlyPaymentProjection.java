package com.epsel.epsel_api.modules.payments.projections;

import java.math.BigDecimal;

public interface MonthlyPaymentProjection {

    Integer getMonth();

    BigDecimal getTotal();

}