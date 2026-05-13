package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface WaterTariffConfigurationRepository extends JpaRepository<WaterTariffConfiguration, UUID> {

    Optional<WaterTariffConfiguration>
    findFirstByZoneAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            ServiceZone zone,
            LocalDate date
    );

}