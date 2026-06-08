package com.epsel.epsel_api.modules.properties.controller;

import com.epsel.epsel_api.modules.properties.dto.CreatePropertyDTO;
import com.epsel.epsel_api.modules.properties.dto.PropertyKpisDTO;
import com.epsel.epsel_api.modules.properties.dto.PropertyResponseDTO;
import com.epsel.epsel_api.modules.properties.dto.UpdatePropertyDTO;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.properties.services.PropertyService;
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
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> create(
            @Valid @RequestBody CreatePropertyDTO dto) {

        PropertyResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse
                        .<PropertyResponseDTO>builder()
                        .success(true)
                        .message("Propiedad creada exitosamente")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> update(
            @PathVariable UUID id,
            @RequestBody UpdatePropertyDTO dto) {

        PropertyResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(
                ApiResponse
                        .<PropertyResponseDTO>builder()
                        .success(true)
                        .message("Propiedad actualizada exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponseDTO>> getById(@PathVariable UUID id) {

        PropertyResponseDTO response = service.getById(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<PropertyResponseDTO>builder()
                        .success(true)
                        .message("Propiedad obtenida exitosamente")
                        .data(response)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PropertyResponseDTO>>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) UUID customerId,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {

        Page<PropertyResponseDTO> response = service.search(search, type, customerId, pageable);

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<PropertyResponseDTO>>builder()
                        .success(true)
                        .message("Propiedades obtenidas exitosamente")
                        .data(response)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message("Propiedad eliminada exitosamente")
                        .build());
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<PropertyKpisDTO>> getKpis() {
        PropertyKpisDTO response = service.getKpis();

        return ResponseEntity.ok(
                ApiResponse
                        .<PropertyKpisDTO>builder()
                        .success(true)
                        .message("KPIs de propiedades obtenidos exitosamente")
                        .data(response)
                        .build());
    }

    @PostMapping("/import/preview")
    public ResponseEntity<ApiResponse<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreatePropertyDTO>>> previewImport(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreatePropertyDTO> response = service.previewImport(file);
        return ResponseEntity.ok(
                ApiResponse.<com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreatePropertyDTO>>builder()
                        .success(true)
                        .message("Previsualización de importación generada")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> createBulk(@Valid @RequestBody java.util.List<CreatePropertyDTO> dtos) {
        service.createBulk(dtos);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Predios importados exitosamente")
                        .data(null)
                        .build()
        );
    }
}