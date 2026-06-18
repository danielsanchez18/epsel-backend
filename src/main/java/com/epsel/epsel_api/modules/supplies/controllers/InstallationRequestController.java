package com.epsel.epsel_api.modules.supplies.controllers;

import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.dto.ApplicationKpisDTO;
import com.epsel.epsel_api.modules.supplies.services.InstallationRequestService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;

@RestController
@RequestMapping("/installation-requests")
@RequiredArgsConstructor
public class InstallationRequestController {

    private final InstallationRequestService service;

    @PostMapping
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> create(
            @Valid @RequestBody CreateInstallationRequestDTO dto) {

        InstallationRequestResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    ApiResponse
                            .<InstallationRequestResponseDTO>builder()
                            .success(true)
                            .message("Instalación solicitada exitosamente")
                            .data(response)
                            .build()
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> approve(
            @PathVariable UUID id) {

        InstallationRequestResponseDTO response = service.approve(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<InstallationRequestResponseDTO>builder()
                        .success(true)
                        .message("Solicitud de instalación aprobada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> reject(
            @PathVariable UUID id,
            @RequestParam String observations) {

        InstallationRequestResponseDTO response = service.reject(id, observations);

        return ResponseEntity.ok(
                ApiResponse
                        .<InstallationRequestResponseDTO>builder()
                        .success(true)
                        .message("Solicitud de instalación rechazada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/install")
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> install(
            @PathVariable UUID id,
            @RequestBody InstallSupplyDTO dto) {

        InstallationRequestResponseDTO response = service.install(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<InstallationRequestResponseDTO>builder()
                        .success(true)
                        .message("Instalación realizada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InstallationRequestResponseDTO>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InstallationRequestStatus status,
            @RequestParam(required = false) String zoneName,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {

        Page<InstallationRequestResponseDTO> response = service.findAll(search, status, zoneName, startDate, endDate, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<InstallationRequestResponseDTO>>builder()
                        .success(true)
                        .message("Solicitudes recuperadas exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> getById(@PathVariable UUID id) {

        InstallationRequestResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<InstallationRequestResponseDTO>builder()
                        .success(true)
                        .message("Solicitud recuperada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/import/preview")
    public ResponseEntity<ApiResponse<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateInstallationRequestDTO>>> previewImport(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateInstallationRequestDTO> response = service.previewImport(file);
        return ResponseEntity.ok(
                ApiResponse.<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateInstallationRequestDTO>>builder()
                        .success(true)
                        .message("Previsualización de importación generada")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> createBulk(@Valid @RequestBody java.util.List<CreateInstallationRequestDTO> dtos) {
        service.createBulk(dtos);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Solicitudes importadas exitosamente")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<ApplicationKpisDTO>> getKpis(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate
    ) {
        ApplicationKpisDTO response = service.getKpis(startDate, endDate);

        return ResponseEntity.ok(
                ApiResponse
                        .<ApplicationKpisDTO>builder()
                        .success(true)
                        .message("KPIs de solicitudes obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }
}
