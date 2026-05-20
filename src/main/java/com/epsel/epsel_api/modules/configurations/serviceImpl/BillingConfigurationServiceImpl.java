package com.epsel.epsel_api.modules.configurations.serviceImpl;

import com.epsel.epsel_api.modules.configurations.dto.BillingConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateBillingConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.entities.BillingConfiguration;
import com.epsel.epsel_api.modules.configurations.repositories.BillingConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.service.BillingConfigurationService;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingConfigurationServiceImpl implements BillingConfigurationService {

    private final BillingConfigurationRepository repository;

    @Override
    public BillingConfigurationResponseDTO getCurrent() {

        BillingConfiguration configuration = repository.findFirstByActiveTrue()
                        .orElseThrow(() -> new ResourceNotFoundException("Configuración de facturación no encontrada"));

        return mapResponse(configuration);
    }

    @Override
    public BillingConfigurationResponseDTO update(UpdateBillingConfigurationDTO dto) {

        BillingConfiguration configuration = repository.findFirstByActiveTrue()
                        .orElseThrow(() -> new ResourceNotFoundException("Configuración de facturación no encontrada"));

        configuration.setMonthsBeforeCut(dto.getMonthsBeforeCut());
        configuration.setLateInterestPercentage(dto.getLateInterestPercentage());
        configuration.setGraceDays(dto.getGraceDays());

        BillingConfiguration updated = repository.save(configuration);

        return mapResponse(updated);
    }

    private BillingConfigurationResponseDTO mapResponse(BillingConfiguration configuration) {

        return BillingConfigurationResponseDTO.builder()
                .id(configuration.getId())
                .monthsBeforeCut(configuration.getMonthsBeforeCut())
                .lateInterestPercentage(configuration.getLateInterestPercentage())
                .graceDays(configuration.getGraceDays())
                .active(configuration.getActive())
                .build();
    }
}