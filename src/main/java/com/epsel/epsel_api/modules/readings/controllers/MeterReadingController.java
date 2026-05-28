package com.epsel.epsel_api.modules.readings.controllers;

import com.epsel.epsel_api.modules.readings.dto.CreateMeterReadingDTO;
import com.epsel.epsel_api.modules.readings.dto.MeterReadingResponseDTO;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.services.MeterReadingService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MeterReadingResponseDTO>> create(
            @Valid @RequestPart("data") CreateMeterReadingDTO dto,
            @RequestPart(value = "meterPhoto", required = false) MultipartFile meterPhoto) {

        MeterReadingResponseDTO response = service.create(dto, meterPhoto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<MeterReadingResponseDTO>builder()
                        .success(true)
                        .message("Lectura de medidor creada exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeterReadingResponseDTO>> getById(@PathVariable UUID id) {

        MeterReadingResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<MeterReadingResponseDTO>builder()
                        .success(true)
                        .message("Lectura de medidor obtenida exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MeterReadingResponseDTO>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID zoneId,
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<MeterReadingResponseDTO> response = service.search(search, zoneId, status, startDate, endDate, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<MeterReadingResponseDTO>>builder()
                        .success(true)
                        .message("Lecturas de medidor obtenidas exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<MeterReadingResponseDTO>> validate(@PathVariable UUID id) {
        MeterReadingResponseDTO response = service.validate(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<MeterReadingResponseDTO>builder()
                        .success(true)
                        .message("Lectura de medidor validada exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<MeterReadingResponseDTO>> cancel(
            @PathVariable UUID id,
            @RequestParam(required = false) String observations) {

        MeterReadingResponseDTO response = service.cancel(id, observations);

        return ResponseEntity.ok(
                ApiResponse
                        .<MeterReadingResponseDTO>builder()
                        .success(true)
                        .message("Lectura de medidor cancelada exitosamente")
                        .data(response)
                        .build());
    }

}