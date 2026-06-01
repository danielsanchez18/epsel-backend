package com.epsel.epsel_api.modules.incidents.service;

import com.epsel.epsel_api.modules.incidents.dto.response.IncidentResponseDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.CreateIncidentDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.ResolveIncidentDTO;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface IncidentService {

    IncidentResponseDTO create(
            CreateIncidentDTO dto
    );

    IncidentResponseDTO getById(
            UUID id
    );

    Page<IncidentResponseDTO> search(
            IncidentStatus status,
            IncidentPriority priority,
            IncidentType type,
            UUID customerId,
            UUID supplyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    IncidentResponseDTO startProgress(
            UUID id
    );

    IncidentResponseDTO resolve(
            UUID id,
            ResolveIncidentDTO dto
    );

    IncidentResponseDTO reject(
            UUID id,
            ResolveIncidentDTO dto
    );

    IncidentResponseDTO close(
            UUID id
    );

}
