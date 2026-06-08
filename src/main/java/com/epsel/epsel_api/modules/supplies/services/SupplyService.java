package com.epsel.epsel_api.modules.supplies.services;

import com.epsel.epsel_api.modules.supplies.dto.ReconnectSupplyDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyDetailsDTO;
import com.epsel.epsel_api.modules.supplies.dto.SupplyResponseDTO;
import com.epsel.epsel_api.modules.supplies.dto.SuspendSupplyDTO;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupplyService {

    Page<SupplyResponseDTO> findAll(
            String search,
            SupplyStatus status,
            UUID zoneId,
            Pageable pageable
    );

    SupplyDetailsDTO getById(UUID id);

    Page<SupplyDetailsDTO> getByCustomerId(UUID customerId, Pageable pageable);

    Page<SupplyDetailsDTO> getByPropertyId(UUID propertyId, Pageable pageable);

    SupplyDetailsDTO getByInstallationRequestId(UUID installationRequestId);

    SupplyResponseDTO suspend(
            UUID id,
            SuspendSupplyDTO dto
    );

    SupplyResponseDTO reconnect(
            UUID id,
            ReconnectSupplyDTO dto
    );

    SupplyResponseDTO cutOff(
            UUID id,
            SuspendSupplyDTO dto
    );

    com.epsel.epsel_api.shared.responses.ImportPreviewResponse<com.epsel.epsel_api.modules.supplies.dto.CreateSupplyBulkDTO> previewImport(org.springframework.web.multipart.MultipartFile file);

    void createBulk(java.util.List<com.epsel.epsel_api.modules.supplies.dto.CreateSupplyBulkDTO> dtos);

    com.epsel.epsel_api.modules.supplies.dto.SupplyKpisDTO getKpis();
}
