package com.epsel.epsel_api.modules.customers.controllers;

import com.epsel.epsel_api.modules.customers.dto.CreateCustomerDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerKpisDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerDetailKpisDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerResponseDTO;
import com.epsel.epsel_api.modules.customers.dto.UpdateCustomerDTO;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import com.epsel.epsel_api.modules.customers.services.CustomerService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> create(
            @Valid @RequestBody CreateCustomerDTO dto) {

        CustomerResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<CustomerResponseDTO>builder()
                        .success(true)
                        .message("Cliente creado exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> update(
            @PathVariable UUID id,
            @RequestBody UpdateCustomerDTO dto) {

        CustomerResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<CustomerResponseDTO>builder()
                        .success(true)
                        .message("Cliente actualizado exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getById(
            @PathVariable UUID id) {

        CustomerResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<CustomerResponseDTO>builder()
                        .success(true)
                        .message("Cliente obtenido exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponseDTO>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CustomerType type,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {

        Page<CustomerResponseDTO> response = service.search(search, type, startDate, endDate, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<CustomerResponseDTO>>builder()
                        .success(true)
                        .message("Clientes obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id ) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message("Cliente eliminado exitosamente")
                        .build()
        );
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<CustomerKpisDTO>> getKpis() {
        CustomerKpisDTO response = service.getKpis();

        return ResponseEntity.ok(
                ApiResponse
                        .<CustomerKpisDTO>builder()
                        .success(true)
                        .message("KPIs de clientes obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}/kpis")
    public ResponseEntity<ApiResponse<CustomerDetailKpisDTO>> getDetailKpis(@PathVariable UUID id) {
        CustomerDetailKpisDTO response = service.getDetailKpis(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<CustomerDetailKpisDTO>builder()
                        .success(true)
                        .message("KPIs de detalle de cliente obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/import/preview")
    public ResponseEntity<ApiResponse<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateCustomerDTO>>> previewImport(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateCustomerDTO> response = service.previewImport(file);
        return ResponseEntity.ok(
                ApiResponse.<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateCustomerDTO>>builder()
                        .success(true)
                        .message("Previsualización de importación generada")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> createBulk(@Valid @RequestBody java.util.List<CreateCustomerDTO> dtos) {
        service.createBulk(dtos);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Clientes importados exitosamente")
                        .data(null)
                        .build()
        );
    }
}