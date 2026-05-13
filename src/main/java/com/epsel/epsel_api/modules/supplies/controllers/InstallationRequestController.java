package com.epsel.epsel_api.modules.supplies.controllers;

import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.services.InstallationRequestService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<ApiResponse<InstallationRequestResponseDTO>> install(@PathVariable UUID id) {

        InstallationRequestResponseDTO response = service.install(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<InstallationRequestResponseDTO>builder()
                        .success(true)
                        .message("Instalación realizada exitosamente")
                        .data(response)
                        .build()
        );
    }

}
