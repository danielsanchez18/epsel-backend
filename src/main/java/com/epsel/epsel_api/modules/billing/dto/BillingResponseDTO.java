package com.epsel.epsel_api.modules.billing.dto;

import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BillingResponseDTO {

    private UUID id;
    private UUID supplyId;
    private String supplyNumber;
    private UUID readingId;
    private Integer consumption;
    private BigDecimal unitPrice;
    private BigDecimal fixedCharge;
    private BigDecimal taxPercentage;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDate billingDate;
    private LocalDate dueDate;
    private BillingStatus status;

}