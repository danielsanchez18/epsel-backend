package com.epsel.epsel_api.modules.supplyOperation.service;

import com.epsel.epsel_api.modules.supplyOperation.dto.response.SupplyOperationResponseDTO;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface SupplyOperationService {

    SupplyOperationResponseDTO getById(UUID id);

    Page<SupplyOperationResponseDTO> search(
            UUID supplyId,
            SupplyOperationType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

}