package com.epsel.epsel_api.modules.configurations.controllers;

import com.epsel.epsel_api.modules.configurations.dto.CreateWaterTariffConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.WaterTariffConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.service.WaterTariffConfigurationService;
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
@RequestMapping("/configurations/water-tariffs")
@RequiredArgsConstructor
public class WaterTariffConfigurationController {

    private final WaterTariffConfigurationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<WaterTariffConfigurationResponseDTO>> create(
            @Valid @RequestBody CreateWaterTariffConfigurationDTO dto) {

        WaterTariffConfigurationResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<WaterTariffConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Tarifa creada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WaterTariffConfigurationResponseDTO>> getById(@PathVariable UUID id) {

        WaterTariffConfigurationResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<WaterTariffConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Tarifa recuperada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WaterTariffConfigurationResponseDTO>>> getAll(
            @RequestParam(required = false) String zoneName,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<WaterTariffConfigurationResponseDTO> response = service.getAll(zoneName, active, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<WaterTariffConfigurationResponseDTO>>builder()
                        .success(true)
                        .message("Tarifas recuperadas exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<Void>> disable(@PathVariable UUID id) {

        service.disable(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message("Tarifa deshabilitada exitosamente")
                        .build()
        );
    }
}
