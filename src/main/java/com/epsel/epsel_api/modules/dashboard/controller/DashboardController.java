package com.epsel.epsel_api.modules.dashboard.controller;

import com.epsel.epsel_api.modules.dashboard.dto.DashboardResponseDTO;
import com.epsel.epsel_api.modules.dashboard.service.DashboardService;
import com.epsel.epsel_api.shared.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboard(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer month,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                ApiResponse.<DashboardResponseDTO>builder()
                        .success(true)
                        .message("Dashboard obtenido exitosamente")
                        .data(service.getDashboard(month, year))
                        .build()
        );
    }

}
