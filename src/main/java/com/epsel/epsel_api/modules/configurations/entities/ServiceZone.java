package com.epsel.epsel_api.modules.configurations.entities;

import com.epsel.epsel_api.modules.configurations.enums.ServiceZoneType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "service_zones")
@Getter
@Setter
public class ServiceZone extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ServiceZoneType name;

    @Column(nullable = false)
    private String description;

}