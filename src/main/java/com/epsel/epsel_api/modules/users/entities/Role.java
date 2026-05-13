package com.epsel.epsel_api.modules.users.entities;

import com.epsel.epsel_api.modules.users.enums.RoleType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    @Column(length = 300)
    private String description;

}
