package com.epsel.epsel_api.modules.properties.repositories;

import com.epsel.epsel_api.modules.properties.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends
        JpaRepository<Property, UUID>,
        JpaSpecificationExecutor<Property> {

    Optional<Property> findByIdAndDeletedFalse(UUID id);

    boolean existsByCadastralCode(String cadastralCode);

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);

    Optional<Property> findByCadastralCode(String cadastralCode);

}
