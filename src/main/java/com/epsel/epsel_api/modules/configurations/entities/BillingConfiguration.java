package com.epsel.epsel_api.modules.configurations.entities;

import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "billing_configurations")
@Getter
@Setter
public class BillingConfiguration extends BaseEntity {

    /* Meses sin cobrar antes del recorte */
    @Column(nullable = false)
    private Integer monthsBeforeCut;

    /* Intereses de demora mensuales % */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal lateInterestPercentage;

    /* Días de plazo */
    @Column(nullable = false)
    private Integer graceDays;

    @Column(nullable = false)
    private Boolean active = true;

}