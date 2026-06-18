package com.epsel.epsel_api.modules.supplies.services;

import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstallationRequestService {

    InstallationRequestResponseDTO create(CreateInstallationRequestDTO dto);

    InstallationRequestResponseDTO approve(UUID id);

    InstallationRequestResponseDTO reject(UUID id, String observations);

    InstallationRequestResponseDTO install(UUID id, InstallSupplyDTO dto);

    Page<InstallationRequestResponseDTO> findAll(
            String search,
            InstallationRequestStatus status,
            String zoneName,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate,
            Pageable pageable
    );

    InstallationRequestResponseDTO getById(UUID id);

    com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateInstallationRequestDTO> previewImport(org.springframework.web.multipart.MultipartFile file);

    void createBulk(java.util.List<CreateInstallationRequestDTO> dtos);

    com.epsel.epsel_api.modules.supplies.dto.ApplicationKpisDTO getKpis(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}
