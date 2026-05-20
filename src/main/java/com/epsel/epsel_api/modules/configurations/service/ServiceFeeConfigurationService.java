package com.epsel.epsel_api.modules.configurations.service;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceFeeConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ServiceFeeConfigurationService {

    ServiceFeeConfigurationResponseDTO create(CreateServiceFeeConfigurationDTO dto);

    ServiceFeeConfigurationResponseDTO update(UUID id, UpdateServiceFeeConfigurationDTO dto);

    ServiceFeeConfigurationResponseDTO getById(UUID id);

    Page<ServiceFeeConfigurationResponseDTO> getAll(
            UUID zoneId,
            ServiceFeeType feeType,
            Boolean active,
            Pageable pageable
    );

    void disable(UUID id);

}
