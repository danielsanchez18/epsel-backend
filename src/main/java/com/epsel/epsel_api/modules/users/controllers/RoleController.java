package com.epsel.epsel_api.modules.users.controllers;

import com.epsel.epsel_api.modules.users.dto.RoleResponseDTO;
import com.epsel.epsel_api.modules.users.services.RoleService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAll() {

        List<RoleResponseDTO> response = roleService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<RoleResponseDTO>>builder()
                        .success(true)
                        .message("Roles obtenidos exitosamente")
                        .data(response)
                        .build()
        );
    }
}
