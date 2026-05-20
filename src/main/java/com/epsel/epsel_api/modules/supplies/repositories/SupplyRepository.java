package com.epsel.epsel_api.modules.supplies.repositories;

import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplyRepository extends JpaRepository<Supply, UUID> {

    Boolean existsByPropertyAndDeletedFalse(Property property);

}
