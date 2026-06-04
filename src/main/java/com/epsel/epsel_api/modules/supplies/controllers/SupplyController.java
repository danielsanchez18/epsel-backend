package com.epsel.epsel_api.modules.supplies.controllers;

import com.epsel.epsel_api.modules.supplies.dto.ReconnectSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyDetailsDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyKpisDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyResponseDTO;
import com.epsel.epsel_api.modules.supplies.dto.SuspendSupplyDTO;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.services.SupplyService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplyResponseDTO>>> findAll(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            SupplyStatus status,

            @RequestParam(required = false)
            UUID zoneId,

            @RequestParam(defaultValue = "0")
            Integer page,

            @RequestParam(defaultValue = "10")
            Integer size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<SupplyResponseDTO> response =
                service.findAll(
                        search,
                        status,
                        zoneId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<SupplyResponseDTO>>builder()
                        .success(true)
                        .message("Suministros encontrados")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplyDetailsDTO>> getById(
            @PathVariable UUID id
    ) {

        SupplyDetailsDTO response =
                service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyDetailsDTO>builder()
                        .success(true)
                        .message("Suministro encontrado")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<SupplyDetailsDTO>>> getByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<SupplyDetailsDTO> response =
                service.getByCustomerId(customerId, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<SupplyDetailsDTO>>builder()
                        .success(true)
                        .message("Suministros del cliente encontrados")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiResponse<Page<SupplyDetailsDTO>>> getByPropertyId(
            @PathVariable UUID propertyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<SupplyDetailsDTO> response =
                service.getByPropertyId(propertyId, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<SupplyDetailsDTO>>builder()
                        .success(true)
                        .message("Suministros de la propiedad encontrados")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/installation-request/{installationRequestId}")
    public ResponseEntity<ApiResponse<SupplyDetailsDTO>> getByInstallationRequestId(
            @PathVariable UUID installationRequestId
    ) {

        SupplyDetailsDTO response =
                service.getByInstallationRequestId(installationRequestId);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyDetailsDTO>builder()
                        .success(true)
                        .message("Suministro encontrado para la solicitud de instalación")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<SupplyResponseDTO>> suspend(
            @PathVariable UUID id,
            @RequestBody @Valid SuspendSupplyDTO dto
    ) {

        SupplyResponseDTO response =
                service.suspend(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyResponseDTO>builder()
                        .success(true)
                        .message("Suministro suspendido")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/cut-off")
    public ResponseEntity<ApiResponse<SupplyResponseDTO>> cutOff(
            @PathVariable UUID id,
            @RequestBody @Valid SuspendSupplyDTO dto
    ) {

        SupplyResponseDTO response =
                service.cutOff(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyResponseDTO>builder()
                        .success(true)
                        .message("Suministro cortado")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/reconnect")
    public ResponseEntity<ApiResponse<SupplyResponseDTO>> reconnect(
            @PathVariable UUID id,
            @RequestBody @Valid ReconnectSupplyDTO dto
    ) {

        SupplyResponseDTO response =
                service.reconnect(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyResponseDTO>builder()
                        .success(true)
                        .message("Suministro reconectado")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<SupplyKpisDTO>> getKpis() {
        SupplyKpisDTO response = service.getKpis();

        return ResponseEntity.ok(
                ApiResponse
                        .<SupplyKpisDTO>builder()
                        .success(true)
                        .message("KPIs de suministros obtenidos")
                        .data(response)
                        .build()
        );
    }
}