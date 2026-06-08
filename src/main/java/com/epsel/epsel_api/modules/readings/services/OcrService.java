package com.epsel.epsel_api.modules.readings.services;

import com.epsel.epsel_api.modules.readings.dto.OcrResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    OcrResponseDTO processMeterImage(MultipartFile file);
}
