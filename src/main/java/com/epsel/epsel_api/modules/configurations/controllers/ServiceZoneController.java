package com.epsel.epsel_api.modules.configurations.controllers;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceZoneDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceZoneResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceZoneDTO;
import com.epsel.epsel_api.modules.configurations.service.ServiceZoneService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/configurations/service-zones")
@RequiredArgsConstructor
public class ServiceZoneController {

    private final ServiceZoneService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceZoneResponseDTO>> create(
            @Valid @RequestBody CreateServiceZoneDTO dto) {

        ServiceZoneResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                        ApiResponse
                                .<ServiceZoneResponseDTO>builder()
                                .success(true)
                                .message("Zona creada exitosamente")
                                .data(response)
                                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceZoneResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceZoneDTO dto) {

        ServiceZoneResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<ServiceZoneResponseDTO>builder()
                        .success(true)
                        .message("Zona actualizada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceZoneResponseDTO>> getById(@PathVariable UUID id) {

        ServiceZoneResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<ServiceZoneResponseDTO>builder()
                        .success(true)
                        .message("Zona recuperada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceZoneResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceZoneResponseDTO> response = service.getAll(search, active, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<ServiceZoneResponseDTO>>builder()
                        .success(true)
                        .message("Zonas recuperadas exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/change-status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable UUID id, @RequestParam Boolean active) {

        service.changeStatus(id, active);

        String status = active ? "activada" : "desactivada";

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message("Zona " + status + " exitosamente")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message("Zona eliminada exitosamente")
                        .build()
        );
    }
}
