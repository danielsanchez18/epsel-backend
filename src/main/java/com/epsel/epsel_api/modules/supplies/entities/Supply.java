package com.epsel.epsel_api.modules.supplies.entities;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "supplies")
@Getter
@Setter
public class Supply extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String supplyNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(optional = false)
    @JoinColumn(name = "installation_request_id")
    private InstallationRequest installationRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplyStatus status;

    @Column(nullable = false)
    private Boolean connected;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType supplyType;

    // Serie física del medidor, debe ser única
    @Column(nullable = false, unique = true, length = 50)
    private String meterNumber;

    // Referencia interna: Piso 1, Tienda, Dpto 201, etc
    @Column(length = 100)
    private String internalReference;

    @Column(nullable = false)
    private LocalDate installationDate;

    private LocalDate activationDate;
    private LocalDate cutOffDate;
    private LocalDate reconnectionDate;

    @Column(length = 500)
    private String cutOffReason;

    private Integer lastReading;

}