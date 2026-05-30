package com.epsel.epsel_api.modules.billing.entities;

import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billings")
@Getter
@Setter
public class Billing extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String billingNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @OneToOne(optional = false)
    @JoinColumn(name = "reading_id")
    private MeterReading reading;

    @OneToMany(mappedBy = "billing")
    private List<Payment> payments = new ArrayList<>();

    @Column(nullable = false)
    private Integer billingMonth;

    @Column(nullable = false)
    private Integer billingYear;

    @Column(nullable = false)
    private Integer consumption;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fixedCharge;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lateFeeAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pendingAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate billingDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate paidDate;

    private LocalDate cancelledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus status;

    @Column(nullable = false)
    private Boolean printed = false;

    @Column(length = 500)
    private String cancelledReason;

}