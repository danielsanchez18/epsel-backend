package com.epsel.epsel_api.modules.incidents.controller;

import com.epsel.epsel_api.modules.incidents.dto.response.IncidentResponseDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.CreateIncidentDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.ResolveIncidentDTO;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import com.epsel.epsel_api.modules.incidents.service.IncidentService;
import com.epsel.epsel_api.shared.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> create(
            @Valid @RequestBody CreateIncidentDTO dto) {

        IncidentResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente creado exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<com.epsel.epsel_api.modules.incidents.dto.IncidentKpiDTO>> getKpis(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate
    ) {
        return ResponseEntity.ok(
                ApiResponse
                        .<com.epsel.epsel_api.modules.incidents.dto.IncidentKpiDTO>builder()
                        .success(true)
                        .message("KPIs de incidencias obtenidos exitosamente")
                        .data(service.getKpis(startDate, endDate))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> getById(@PathVariable UUID id) {

        IncidentResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente obtenido exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<IncidentResponseDTO>>> search(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentPriority priority,
            @RequestParam(required = false) IncidentType type,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID supplyId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<IncidentResponseDTO> response = service.search(
                status, priority, type, customerId, supplyId, startDate, endDate, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<IncidentResponseDTO>>builder()
                        .success(true)
                        .message("Incidentes obtenidos exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/start-progress")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> startProgress(@PathVariable UUID id) {

        IncidentResponseDTO response = service.startProgress(id);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente en progreso")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> resolve(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveIncidentDTO dto) {

        IncidentResponseDTO response = service.resolve(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente resuelto exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> reject(
            @PathVariable UUID id,
            @Valid @RequestBody ResolveIncidentDTO dto) {

        IncidentResponseDTO response = service.reject(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente rechazado exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<IncidentResponseDTO>> close(@PathVariable UUID id) {

        IncidentResponseDTO response = service.close(id);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponseDTO>builder()
                        .success(true)
                        .message("Incidente cerrado exitosamente")
                        .data(response)
                        .build());
    }
}

