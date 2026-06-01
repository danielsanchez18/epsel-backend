package com.epsel.epsel_api.modules.incidents.entity;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "incidents")
@Getter
@Setter
public class Incident extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String incidentNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private LocalDate reportedDate;

    private LocalDate resolvedDate;

    @Column(length = 3000)
    private String resolution;
}