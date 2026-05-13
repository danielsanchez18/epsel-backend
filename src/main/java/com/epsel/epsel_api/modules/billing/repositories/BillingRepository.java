package com.epsel.epsel_api.modules.billing.repositories;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BillingRepository extends
        JpaRepository<Billing, UUID>,
        JpaSpecificationExecutor<Billing> {

    boolean existsByReading(MeterReading reading);

    Optional<Billing> findByReading(MeterReading reading);

    long countBySupplyAndDeletedFalse(Supply supply);

    Page<Billing> findBySupplyIdAndDeletedFalse(UUID supplyId, Pageable pageable);

}