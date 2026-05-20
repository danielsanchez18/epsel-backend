package com.epsel.epsel_api.modules.configurations.service;

import com.epsel.epsel_api.modules.configurations.dto.CreateWaterTariffConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.WaterTariffConfigurationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WaterTariffConfigurationService {

    WaterTariffConfigurationResponseDTO create(CreateWaterTariffConfigurationDTO dto);

    WaterTariffConfigurationResponseDTO getById(UUID id);

    Page<WaterTariffConfigurationResponseDTO> getAll(
            String zoneName,
            Boolean active,
            Pageable pageable
    );

    void disable(UUID id);

}