package com.epsel.epsel_api.modules.readings.repositories;

import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MeterReadingRepository extends
        JpaRepository<MeterReading, UUID>,
        JpaSpecificationExecutor<MeterReading> {

    Optional<MeterReading> findTopBySupplyOrderByReadingDateDesc(Supply supply);

}
