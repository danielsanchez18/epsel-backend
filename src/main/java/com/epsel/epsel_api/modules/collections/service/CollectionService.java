package com.epsel.epsel_api.modules.collections.service;

import com.epsel.epsel_api.shared.responses.ApiResponse;
import com.epsel.epsel_api.modules.collections.dto.CollectionKpiDTO;

import java.time.LocalDateTime;

public interface CollectionService {
    ApiResponse<CollectionKpiDTO> getKpis(LocalDateTime startDate, LocalDateTime endDate);
}
