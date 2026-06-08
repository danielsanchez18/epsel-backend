package com.epsel.epsel_api.modules.ocr.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OCRScanResponseDTO {

    private UUID id;
    private UUID supplyId;
    private String supplyNumber;
    private String imageUrl;
    private Integer previousReading;
    private Integer detectedReading;
    private Double confidence;
    private Boolean anomaly;
    private LocalDateTime scannedAt;
    private String observations;
    private List<String> detectedTexts;

}