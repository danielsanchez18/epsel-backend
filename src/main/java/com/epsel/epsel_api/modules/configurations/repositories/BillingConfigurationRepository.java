package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.BillingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillingConfigurationRepository extends JpaRepository<BillingConfiguration, UUID> {

    Optional<BillingConfiguration> findFirstByActiveTrue();

}
