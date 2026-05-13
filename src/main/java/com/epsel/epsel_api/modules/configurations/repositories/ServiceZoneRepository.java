package com.epsel.epsel_api.modules.configurations.repositories;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.enums.ServiceZoneType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceZoneRepository extends JpaRepository<ServiceZone, UUID> {

    Optional<ServiceZone> findByName(ServiceZoneType name);

}