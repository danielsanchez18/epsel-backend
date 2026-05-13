package com.epsel.epsel_api.modules.properties.entities;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Column(nullable = false)
    private String address;

    @Column
    private String reference;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_id")
    private ServiceZone zone;

}