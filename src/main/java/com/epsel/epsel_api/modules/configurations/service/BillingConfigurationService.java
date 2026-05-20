package com.epsel.epsel_api.modules.configurations.service;

import com.epsel.epsel_api.modules.configurations.dto.BillingConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateBillingConfigurationDTO;

public interface BillingConfigurationService {

    BillingConfigurationResponseDTO getCurrent();

    BillingConfigurationResponseDTO update(UpdateBillingConfigurationDTO dto);

}
