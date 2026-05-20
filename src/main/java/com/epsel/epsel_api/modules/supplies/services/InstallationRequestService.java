package com.epsel.epsel_api.modules.supplies.services;

import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstallationRequestService {

    InstallationRequestResponseDTO create(CreateInstallationRequestDTO dto);

    InstallationRequestResponseDTO approve(UUID id);

    InstallationRequestResponseDTO reject(UUID id, String observations);

    InstallationRequestResponseDTO install(UUID id);

    Page<InstallationRequestResponseDTO> findAll(
            String search,
            InstallationRequestStatus status,
            String zoneName,
            Pageable pageable
    );

    InstallationRequestResponseDTO getById(UUID id);

}
