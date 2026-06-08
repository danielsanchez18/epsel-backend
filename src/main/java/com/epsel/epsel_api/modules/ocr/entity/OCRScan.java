package com.epsel.epsel_api.modules.ocr.entity;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_scans")
@Getter
@Setter
public class OCRScan extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "supply_id")
    private Supply supply;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer previousReading;

    @Column(nullable = false)
    private Integer detectedReading;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false)
    private Boolean anomaly;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    @Column(length = 500)
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String rawDetectedTexts;

}