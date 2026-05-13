package com.epsel.epsel_api.modules.configurations.entities;

import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "service_fee_configurations")
@Getter
@Setter
public class ServiceFeeConfiguration extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_id")
    private ServiceZone zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceFeeType feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Boolean active = true;

}
