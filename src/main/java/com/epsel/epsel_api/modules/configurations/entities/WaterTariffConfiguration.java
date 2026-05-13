package com.epsel.epsel_api.modules.configurations.entities;

import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "water_tariff_configurations")
@Getter
@Setter
public class WaterTariffConfiguration extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_id")
    private ServiceZone zone;

    /* Precio por metro cúbico */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerM3;

    /* Cargo fijo mensual */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fixedCharge;

    /* Porcentaje de impuesto */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    /* Fecha de vigencia de esta configuración tarifaria.
    * Solo una configuración puede estar activa por zona en una fecha dada.*/
    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private Boolean active = true;
}