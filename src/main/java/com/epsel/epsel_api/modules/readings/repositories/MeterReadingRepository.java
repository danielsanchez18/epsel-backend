package com.epsel.epsel_api.modules.readings.repositories;

import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeterReadingRepository extends
        JpaRepository<MeterReading, UUID>,
        JpaSpecificationExecutor<MeterReading> {

    Boolean existsBySupplyAndReadingDateBetweenAndStatusIn(
            Supply supply,
            LocalDate startDate,
            LocalDate endDate,
            List<ReadingStatus> statuses
    );

    Optional<MeterReading> findTopBySupplyAndStatusInAndIdNotOrderByReadingDateDesc(
            Supply supply,
            List<ReadingStatus> statuses,
            UUID id
    );

    long countByStatusAndDeletedFalse(ReadingStatus status);

    List<MeterReading> findByDeletedFalse(Pageable pageable);

    long countByDeletedFalseAndCreatedAtAfter(java.time.LocalDateTime dateTime);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(mr.consumption), 0) FROM MeterReading mr WHERE mr.deleted = false AND mr.readingDate >= :startDate")
    long sumConsumptionByReadingDateAfterAndDeletedFalse(java.time.LocalDate startDate);
}
