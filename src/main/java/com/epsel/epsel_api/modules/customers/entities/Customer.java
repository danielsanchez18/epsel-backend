package com.epsel.epsel_api.modules.customers.entities;

import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    /* DNI or RUC*/
    @Column(nullable = false, unique = true, length = 20)
    private String documentNumber;

    @Column(nullable = false)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column
    private String email;

}