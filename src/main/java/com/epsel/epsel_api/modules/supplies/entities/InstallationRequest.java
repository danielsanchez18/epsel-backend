package com.epsel.epsel_api.modules.supplies.entities;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "installation_requests")
@Getter
@Setter
public class InstallationRequest extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    /* Precio de instalación aplicado */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal installationCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallationRequestStatus status;

    /* Fecha de instalación solicitada */
    private LocalDate requestedDate;

    /* Fecha de instalación aprobada */
    private LocalDate installationDate;

    @Column(length = 500)
    private String observations;
}