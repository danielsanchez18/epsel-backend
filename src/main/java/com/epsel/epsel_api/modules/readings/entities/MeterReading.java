package com.epsel.epsel_api.modules.readings.entities;

import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "meter_readings")
@Getter
@Setter
public class MeterReading extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    /* Lectura previa del medidor */
    @Column(nullable = false)
    private Integer previousReading;

    /* Lectura actual del medidor */
    @Column(nullable = false)
    private Integer currentReading;

    /* Consumo de agua de m³ */
    @Column(nullable = false)
    private Integer consumption;

    /* Fecha de lectura */
    @Column(nullable = false)
    private LocalDate readingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    @Column(length = 500)
    private String observations;

}
