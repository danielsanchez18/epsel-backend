package com.epsel.epsel_api.modules.configurations.controllers;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceFeeConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.service.ServiceFeeConfigurationService;
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
@RequestMapping("/configurations/service-fees")
@RequiredArgsConstructor
public class ServiceFeeConfigurationController {

    private final ServiceFeeConfigurationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceFeeConfigurationResponseDTO>> create(
            @Valid @RequestBody CreateServiceFeeConfigurationDTO dto) {

        ServiceFeeConfigurationResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<ServiceFeeConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Tarifa de servicio creada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceFeeConfigurationResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceFeeConfigurationDTO dto) {

        ServiceFeeConfigurationResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<ServiceFeeConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Tarifa de servicio actualizada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceFeeConfigurationResponseDTO>> getById(@PathVariable UUID id) {

        ServiceFeeConfigurationResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<ServiceFeeConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Tarifa de servicio recuperada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceFeeConfigurationResponseDTO>>> getAll(
            @RequestParam(required = false) UUID zoneId,
            @RequestParam(required = false) ServiceFeeType feeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceFeeConfigurationResponseDTO> response = service.getAll(zoneId, feeType, active, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<ServiceFeeConfigurationResponseDTO>>builder()
                        .success(true)
                        .message("Tarifas de servicio recuperadas exitosamente")
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
                        .message("Tarifa de servicio deshabilitada exitosamente")
                        .build()
        );
    }
}