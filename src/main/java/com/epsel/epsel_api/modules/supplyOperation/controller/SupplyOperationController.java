package com.epsel.epsel_api.modules.supplyOperation.controller;

import com.epsel.epsel_api.modules.supplyOperation.dto.response.SupplyOperationResponseDTO;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.service.SupplyOperationService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/supply-operations")
@RequiredArgsConstructor
public class SupplyOperationController {

    private final SupplyOperationService service;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplyOperationResponseDTO>> getById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyOperationResponseDTO>builder()
                        .success(true)
                        .message("Operación encontrada")
                        .data(service.getById(id))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplyOperationResponseDTO>>> search(
            @RequestParam(required = false) UUID supplyId,
            @RequestParam(required = false) SupplyOperationType type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<SupplyOperationResponseDTO>>builder()
                        .success(true)
                        .message("Operaciones obtenidas")
                        .data(
                                service.search(
                                        supplyId,
                                        type,
                                        startDate,
                                        endDate,
                                        pageable
                                )
                        )
                        .build()
        );
    }
}
