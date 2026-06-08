package com.epsel.epsel_api.modules.ocr.controller;

import com.epsel.epsel_api.modules.ocr.dto.OCRScanResponseDTO;
import com.epsel.epsel_api.modules.ocr.service.OCRService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OCRController {

    private final OCRService service;

    @PostMapping(value = "/scan",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OCRScanResponseDTO>> scan(
            @RequestParam UUID supplyId,
            @RequestPart MultipartFile file) {

        OCRScanResponseDTO response = service.scan(supplyId, file);

        return ResponseEntity.ok(
                ApiResponse.<OCRScanResponseDTO>builder()
                        .success(true)
                        .message("Lectura OCR procesada")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OCRScanResponseDTO>> getById(@PathVariable UUID id) {

        return ResponseEntity.ok(
                ApiResponse.<OCRScanResponseDTO>builder()
                        .success(true)
                        .message("Escaneo encontrado")
                        .data(service.getById(id))
                        .build());
    }

    @GetMapping("/supply/{supplyId}")
    public ResponseEntity<
            ApiResponse<Page<OCRScanResponseDTO>>> getBySupply(
                    @PathVariable UUID supplyId,
                    @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<OCRScanResponseDTO>>builder()
                        .success(true)
                        .message("Escaneos OCR encontrados")
                        .data(service.getBySupply(supplyId, pageable))
                        .build());
    }
}