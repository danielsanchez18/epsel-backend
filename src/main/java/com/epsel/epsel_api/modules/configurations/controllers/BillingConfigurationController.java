package com.epsel.epsel_api.modules.configurations.controllers;

import com.epsel.epsel_api.modules.configurations.dto.BillingConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateBillingConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.service.BillingConfigurationService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configurations/billing")
@RequiredArgsConstructor
public class BillingConfigurationController {

    private final BillingConfigurationService service;

    @GetMapping
    public ResponseEntity<ApiResponse<BillingConfigurationResponseDTO>> getCurrent() {

        BillingConfigurationResponseDTO response = service.getCurrent();

        return ResponseEntity.ok(
                ApiResponse
                        .<BillingConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Configuración de facturación recuperada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<BillingConfigurationResponseDTO>> update(
            @Valid @RequestBody UpdateBillingConfigurationDTO dto) {

        BillingConfigurationResponseDTO response = service.update(dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<BillingConfigurationResponseDTO>builder()
                        .success(true)
                        .message("Configuración de facturación actualizada exitosamente")
                        .data(response)
                        .build()
        );
    }
}