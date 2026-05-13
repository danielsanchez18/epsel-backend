package com.epsel.epsel_api.modules.supplies.entities;

import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "supplies")
@Getter
@Setter
public class Supply extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String supplyNumber;

    @OneToOne(optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyStatus status;

    /* Si el servicio está conectado o activo actualmente */
    @Column(nullable = false)
    private Boolean connected = false;

}