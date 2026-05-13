package com.epsel.epsel_api.modules.billing.controllers;

import com.epsel.epsel_api.modules.billing.dto.BillingResponseDTO;
import com.epsel.epsel_api.modules.billing.services.BillingService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/billings")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService service;

    @PostMapping("/generate/{readingId}")
    public ResponseEntity<ApiResponse<BillingResponseDTO>> generate(
            @PathVariable UUID readingId) {

        BillingResponseDTO response = service.generate(readingId);

        return ResponseEntity.ok(
                ApiResponse
                        .<BillingResponseDTO>builder()
                        .success(true)
                        .message("Factura generada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillingResponseDTO>> getById(
            @PathVariable UUID id) {

        BillingResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<BillingResponseDTO>builder()
                        .success(true)
                        .message("Factura encontrada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/supply/{supplyId}")
    public ResponseEntity<ApiResponse<Page<BillingResponseDTO>>> getBySupply(
            @PathVariable UUID supplyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BillingResponseDTO> response = service.getBySupply(supplyId, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<BillingResponseDTO>>builder()
                        .success(true)
                        .message("Facturas encontradas exitosamente para este suministro")
                        .data(response)
                        .build()
        );
    }
}