package com.epsel.epsel_api.modules.readings.dto;

import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MeterReadingResponseDTO {

    private String id;

    private String supplyId;
    private String supplyNumber;

    private String customerName;
    private String meterNumber;

    private Integer previousReading;
    private Integer currentReading;
    private Integer consumption;

    private String readingDate;

    private ReadingStatus status;

    private String meterPhotoUrl;

    private String ocrValue;

    private String observations;

}
