package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceFeeConfigurationRepository
        extends JpaRepository<ServiceFeeConfiguration, UUID>,
        JpaSpecificationExecutor<ServiceFeeConfiguration>
{

    Optional<ServiceFeeConfiguration> findByZone_IdAndFeeTypeAndActiveTrue(UUID zoneId, ServiceFeeType feeType);

}