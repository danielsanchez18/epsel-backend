package com.epsel.epsel_api.modules.auth.controllers;

import com.epsel.epsel_api.modules.auth.dtos.AuthResponseDTO;
import com.epsel.epsel_api.modules.auth.dtos.LoginRequestDTO;
import com.epsel.epsel_api.modules.auth.services.AuthService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {

        AuthResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponseDTO>builder()
                        .success(true)
                        .message("Inicio de sesión exitoso")
                        .data(response)
                        .build()
        );
    }
}
