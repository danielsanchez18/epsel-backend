package com.epsel.epsel_api.modules.supplies.entities;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.users.entities.User;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    // Piso 1, Tienda, etc
    @Column(length = 100)
    private String internalReference;

    /* Precio de instalación aplicado */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal installationCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallationRequestStatus status;

    /* Fecha de instalación solicitada */
    private LocalDate requestedDate;

    private LocalDate approvedDate;

    /* Fecha de instalación aprobada */
    private LocalDate installationDate;

    private LocalDate rejectedDate;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @ManyToOne
    @JoinColumn(name = "installed_by")
    private User installedBy;

    @ManyToOne
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;

    @Column(length = 500)
    private String observations;
}