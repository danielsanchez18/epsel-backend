package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceZoneRepository
        extends JpaRepository<ServiceZone, UUID>,
        JpaSpecificationExecutor<ServiceZone> {

    Optional<ServiceZone> findByNameIgnoreCase(String name);

    Optional<ServiceZone> findByIdAndDeletedFalse(UUID zoneId);

}