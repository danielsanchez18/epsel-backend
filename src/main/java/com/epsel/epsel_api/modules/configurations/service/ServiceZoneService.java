package com.epsel.epsel_api.modules.configurations.service;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceZoneDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceZoneResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceZoneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ServiceZoneService {

    ServiceZoneResponseDTO create(CreateServiceZoneDTO dto);

    ServiceZoneResponseDTO update(UUID id, UpdateServiceZoneDTO dto);

    ServiceZoneResponseDTO getById(UUID id);

    Page<ServiceZoneResponseDTO> getAll(
            String search,
            Boolean active,
            Pageable pageable
    );

    void changeStatus(UUID id, Boolean active);

    void delete(UUID id);
}
