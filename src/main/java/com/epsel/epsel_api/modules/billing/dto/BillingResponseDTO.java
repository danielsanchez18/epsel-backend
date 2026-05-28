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

    private String billingNumber;

    private UUID supplyId;
    private String supplyNumber;

    private UUID readingId;

    private String customerName;
    private String propertyAddress;
    private String zoneName;

    private Integer billingMonth;
    private Integer billingYear;

    private Integer consumption;

    private BigDecimal unitPrice;
    private BigDecimal fixedCharge;
    private BigDecimal taxPercentage;

    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal lateFeeAmount;

    private BigDecimal totalAmount;
    private BigDecimal amountPaid;

    private LocalDate billingDate;
    private LocalDate dueDate;

    private BillingStatus status;

    private Boolean printed;

}