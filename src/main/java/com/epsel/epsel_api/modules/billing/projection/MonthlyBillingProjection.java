package com.epsel.epsel_api.modules.billing.projection;

import java.math.BigDecimal;

public interface MonthlyBillingProjection {

    Integer getMonth();

    BigDecimal getTotal();

}