package com.epsel.epsel_api.modules.payments.dto;

import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentResponseDTO {

    private UUID id;

    private String receiptNumber;

    private UUID billingId;
    private String billingNumber;

    private UUID supplyId;
    private String supplyNumber;

    private String customerFullName;

    private BigDecimal amount;

    private BigDecimal billingTotalAmount;
    private BigDecimal billingPendingAmount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private LocalDateTime paymentDate;

    private String operationNumber;

    private String observations;

    private String cancellationReason;
    private LocalDateTime cancelledAt;

    private UUID registeredById;
    private String registeredBy;

}