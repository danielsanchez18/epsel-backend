package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.enums.ServiceZoneType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceFeeConfigurationRepository extends JpaRepository<ServiceFeeConfiguration, UUID> {

    Optional<ServiceFeeConfiguration> findByZone_NameAndFeeTypeAndActiveTrue(ServiceZoneType zone, ServiceFeeType feeType);

}