package com.epsel.epsel_api.modules.readings.services;

import com.epsel.epsel_api.modules.readings.dto.CreateMeterReadingDTO;
import com.epsel.epsel_api.modules.readings.dto.MeterReadingResponseDTO;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface MeterReadingService {

    MeterReadingResponseDTO create(CreateMeterReadingDTO dto);

    MeterReadingResponseDTO getById(UUID id);

    Page<MeterReadingResponseDTO> search(
            UUID supplyId,
            ReadingStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}