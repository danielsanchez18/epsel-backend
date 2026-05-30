package com.epsel.epsel_api.modules.payments.entities;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "billing_id")
    private Billing billing;

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 100)
    private String operationNumber;

    @Column(length = 500)
    private String observations;

    @Column(length = 500)
    private String cancellationReason;

    private LocalDateTime cancelledAt;

    @ManyToOne
    @JoinColumn(name = "registered_by")
    private User registeredBy;

}