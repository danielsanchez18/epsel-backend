package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaterTariffConfigurationRepository
        extends JpaRepository<WaterTariffConfiguration, UUID>,
        JpaSpecificationExecutor<WaterTariffConfiguration> {

    Optional<WaterTariffConfiguration>
    findFirstByZoneAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            ServiceZone zone,
            LocalDate date
    );

    List<WaterTariffConfiguration> findByZoneAndActiveTrueOrderByEffectiveDateDesc(ServiceZone zone);

}