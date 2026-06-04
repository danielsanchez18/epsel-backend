package com.epsel.epsel_api.modules.users.controllers;

import com.epsel.epsel_api.modules.users.dto.CreateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UpdateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UserResponseDTO;
import com.epsel.epsel_api.modules.users.dto.UserSearchDTO;
import com.epsel.epsel_api.modules.users.dto.WorkerKpisDTO;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import com.epsel.epsel_api.modules.users.services.UserService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(
            @Valid @RequestPart("data") CreateUserDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        UserResponseDTO response = userService.create(dto, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<UserResponseDTO>builder()
                                .success(true)
                                .message("Usuario creado exitosamente")
                                .data(response)
                                .build()
                );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestPart("data") UpdateUserDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        UserResponseDTO response = userService.update(id, dto, image);

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("Usuario actualizado exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable UUID id) {

        UserResponseDTO response = userService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("Usuario obtenido exitosamente")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAll(UserSearchDTO searchDTO, Pageable pageable) {

        Page<UserResponseDTO> response = userService.getAll(searchDTO, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponseDTO>>builder()
                        .success(true)
                        .message("Usuarios obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable UUID id,
            @RequestParam UserStatus status
    ) {

        userService.changeStatus(id, status);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Estado del usuario actualizado exitosamente")
                        .data(null)
                        .build()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {

        userService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Usuario eliminado exitosamente")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<WorkerKpisDTO>> getWorkerKpis() {
        WorkerKpisDTO response = userService.getWorkerKpis();
        return ResponseEntity.ok(
                ApiResponse.<WorkerKpisDTO>builder()
                        .success(true)
                        .message("KPIs de personal obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }
}