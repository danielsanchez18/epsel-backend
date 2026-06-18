package com.epsel.epsel_api.modules.readings.services;

import com.epsel.epsel_api.modules.readings.dto.CreateMeterReadingDTO;
import com.epsel.epsel_api.modules.readings.dto.MeterReadingResponseDTO;
import com.epsel.epsel_api.modules.readings.dto.ReadingKpisDTO;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface MeterReadingService {

    MeterReadingResponseDTO create(CreateMeterReadingDTO dto, MultipartFile meterPhoto);

    MeterReadingResponseDTO getById(UUID id);

    Page<MeterReadingResponseDTO> search(
            String search,
            UUID zoneId,
            ReadingStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    MeterReadingResponseDTO validate(UUID id);

    MeterReadingResponseDTO cancel(UUID id, String observations);

    ReadingKpisDTO getKpis(LocalDateTime startDate, LocalDateTime endDate);
}