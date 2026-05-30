package com.epsel.epsel_api.modules.supplyWorkOrder.controller;

import com.epsel.epsel_api.modules.supplyWorkOrder.dto.request.*;
import com.epsel.epsel_api.modules.supplyWorkOrder.dto.response.SupplyWorkOrderResponseDTO;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.service.SupplyWorkOrderService;
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
@RequestMapping("/supply-work-orders")
@RequiredArgsConstructor
public class SupplyWorkOrderController {

    private final SupplyWorkOrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> create(
            @Valid @RequestBody CreateSupplyWorkOrderDTO dto) {

        SupplyWorkOrderResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo creada exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> getById(@PathVariable UUID id) {

        SupplyWorkOrderResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo obtenida exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplyWorkOrderResponseDTO>>> search(
            @RequestParam(required = false) UUID supplyId,
            @RequestParam(required = false) WorkOrderType type,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SupplyWorkOrderResponseDTO> response = service.search(supplyId, type, status, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<SupplyWorkOrderResponseDTO>>builder()
                        .success(true)
                        .message("Órdenes de trabajo obtenidas exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignWorkOrderDTO dto) {

        SupplyWorkOrderResponseDTO response = service.assign(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo asignada exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> start(
            @PathVariable UUID id,
            @Valid @RequestBody StartWorkOrderDTO dto) {

        SupplyWorkOrderResponseDTO response = service.start(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo iniciada exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> complete(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteWorkOrderDTO dto) {

        SupplyWorkOrderResponseDTO response = service.complete(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo completada exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SupplyWorkOrderResponseDTO>> cancel(
            @PathVariable UUID id,
            @Valid @RequestBody CancelWorkOrderDTO dto) {

        SupplyWorkOrderResponseDTO response = service.cancel(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyWorkOrderResponseDTO>builder()
                        .success(true)
                        .message("Orden de trabajo cancelada exitosamente")
                        .data(response)
                        .build());
    }

}

