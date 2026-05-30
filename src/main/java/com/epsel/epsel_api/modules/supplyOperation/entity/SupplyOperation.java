package com.epsel.epsel_api.modules.supplyOperation.entity;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "supply_operations")
@Getter
@Setter
public class SupplyOperation extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyOperationType operationType;

    @Column(nullable = false)
    private LocalDate operationDate;

    @Column(length = 500)
    private String reason;

    @Column(length = 100)
    private String performedBy;

    @Column(length = 500)
    private String observations;

}