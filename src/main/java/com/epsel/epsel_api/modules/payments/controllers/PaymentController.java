package com.epsel.epsel_api.modules.payments.controllers;

import com.epsel.epsel_api.modules.payments.dto.CancelPaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.CreatePaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.PaymentResponseDTO;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.payments.services.PaymentService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> create(
            @Valid @RequestBody CreatePaymentDTO dto) {

        PaymentResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                    .<PaymentResponseDTO>builder()
                    .success(true)
                    .message("Pago registrado exitosamente")
                    .data(response)
                    .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getById(
            @PathVariable UUID id) {

        PaymentResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                    .<PaymentResponseDTO>builder()
                    .success(true)
                    .message("Pago encontrado exitosamente")
                    .data(response)
                    .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponseDTO>>> search(
            @RequestParam(required = false) String receiptNumber,
            @RequestParam(required = false) String billingNumber,
            @RequestParam(required = false) String supplyNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PaymentResponseDTO> response = service.search(
                receiptNumber,
                billingNumber,
                supplyNumber,
                customerName,
                paymentMethod,
                status,
                startDate,
                endDate,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<PaymentResponseDTO>>builder()
                        .success(true)
                        .message("Pagos obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/billing/{billingId}")
    public ResponseEntity<ApiResponse<Page<PaymentResponseDTO>>> getByBilling(
            @PathVariable UUID billingId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PaymentResponseDTO> response = service.getByBilling(billingId, pageable);

        return ResponseEntity.ok(
                ApiResponse
                    .<Page<PaymentResponseDTO>>builder()
                    .success(true)
                    .message("Pagos de la factura encontrados exitosamente")
                    .data(response)
                    .build()
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> cancel(
            @PathVariable UUID id,
            @RequestBody @Valid CancelPaymentDTO dto
    ) {

        PaymentResponseDTO response =
                service.cancel(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<PaymentResponseDTO>builder()
                        .success(true)
                        .message("Pago cancelado exitosamente")
                        .data(response)
                        .build()
        );
    }
}
