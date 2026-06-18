package com.epsel.epsel_api.modules.collections.controller;

import com.epsel.epsel_api.shared.responses.ApiResponse;
import com.epsel.epsel_api.modules.collections.dto.CollectionKpiDTO;
import com.epsel.epsel_api.modules.collections.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<CollectionKpiDTO>> getKpis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(collectionService.getKpis(startDate, endDate));
    }
}
