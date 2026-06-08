package com.epsel.epsel_api.modules.ocr.repository;

import com.epsel.epsel_api.modules.ocr.entity.OCRScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OCRScanRepository extends JpaRepository<OCRScan, UUID> {

    Page<OCRScan> findBySupplyIdAndDeletedFalse(
            UUID supplyId,
            Pageable pageable
    );

}
