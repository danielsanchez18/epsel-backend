package com.epsel.epsel_api.modules.readings.controllers;

import com.epsel.epsel_api.modules.readings.dto.OcrResponseDTO;
import com.epsel.epsel_api.modules.readings.services.OcrService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    @PostMapping(value = "/read", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OcrResponseDTO>> readMeter(
            @RequestPart("file") MultipartFile file) {

        OcrResponseDTO response = ocrService.processMeterImage(file);

        return ResponseEntity.ok(
                ApiResponse
                        .<OcrResponseDTO>builder()
                        .success(true)
                        .message("Imagen procesada por OCR exitosamente")
                        .data(response)
                        .build());
    }
}
