package com.epsel.epsel_api.modules.supplies.repositories;

import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.supplies.dto.SupplyDetailsDTO;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SupplyRepository extends
        JpaRepository<Supply, UUID>,
        JpaSpecificationExecutor<Supply> {

    Optional<Supply> findByIdAndDeletedFalse(UUID id);

    Optional<Supply> findBySupplyNumberAndDeletedFalse(String supplyNumber);

    Boolean existsByPropertyAndDeletedFalse(Property property);

    Boolean existsByMeterNumberAndDeletedFalse(String meterNumber);

    Page<Supply> findByCustomerIdAndDeletedFalse(UUID customerId, Pageable pageable);

    Page<Supply> findByPropertyIdAndDeletedFalse(UUID propertyId, Pageable pageable);

    Optional<Supply> findByInstallationRequestIdAndDeletedFalse(UUID installationRequestId);

}