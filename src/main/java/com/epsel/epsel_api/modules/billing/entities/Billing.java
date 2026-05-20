package com.epsel.epsel_api.modules.billing.entities;

import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "billings")
@Getter
@Setter
public class Billing extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @OneToOne(optional = false)
    @JoinColumn(name = "reading_id")
    private MeterReading reading;

    /* Resumen del consumo */
    @Column(nullable = false)
    private Integer consumption;

    /* Resumen del precio por m³ */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /* Resumen de los gastos fijos */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fixedCharge;

    /* Resumen del porcentaje de impuestos */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    /* Subtotal antes de impuestos */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /* Importe de los impuestos */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    /* Importe total final */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDate billingDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus status;

}