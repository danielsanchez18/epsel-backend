package com.epsel.epsel_api.modules.ocr.service;

import com.epsel.epsel_api.modules.ocr.dto.OCRScanResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface OCRService {

    OCRScanResponseDTO scan(
            UUID supplyId,
            MultipartFile file
    );

    OCRScanResponseDTO getById(
            UUID id
    );

    Page<OCRScanResponseDTO> getBySupply(
            UUID supplyId,
            Pageable pageable
    );

}
