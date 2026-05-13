package com.epsel.epsel_api.modules.readings.dto;

import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MeterReadingResponseDTO {

    private UUID id;
    private UUID supplyId;
    private String supplyNumber;
    private Integer previousReading;
    private Integer currentReading;
    private Integer consumption;
    private LocalDate readingDate;
    private ReadingStatus status;
    private String observations;

}
