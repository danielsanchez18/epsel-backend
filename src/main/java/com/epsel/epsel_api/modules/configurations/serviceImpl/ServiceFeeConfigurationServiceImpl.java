package com.epsel.epsel_api.modules.configurations.serviceImpl;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceFeeConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceFeeConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceFeeConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.configurations.service.ServiceFeeConfigurationService;
import com.epsel.epsel_api.modules.configurations.specifications.ServiceFeeConfigurationSpecification;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceFeeConfigurationServiceImpl implements ServiceFeeConfigurationService {

    private final ServiceFeeConfigurationRepository repository;
    private final ServiceZoneRepository zoneRepository;

    @Override
    public ServiceFeeConfigurationResponseDTO create(CreateServiceFeeConfigurationDTO dto) {

        ServiceZone zone = zoneRepository.findById(dto.getZoneId())
                        .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        if (!zone.getActive()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        repository.findByZone_IdAndFeeTypeAndActiveTrue(
                zone.getId(),
                dto.getFeeType()
        ).ifPresent(existing -> {

                throw new ResourceNotFoundException(
                        "Ya existe una configuración activa para esta zona y tipo de tarifa"
                );
        });

        ServiceFeeConfiguration configuration = new ServiceFeeConfiguration();

        configuration.setZone(zone);
        configuration.setFeeType(dto.getFeeType());
        configuration.setAmount(dto.getAmount());
        configuration.setActive(true);

        ServiceFeeConfiguration saved = repository.save(configuration);

        return mapResponse(saved);
    }

    @Override
    public ServiceFeeConfigurationResponseDTO update(
            UUID id,
            UpdateServiceFeeConfigurationDTO dto
    ) {

        ServiceFeeConfiguration configuration = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio"));

        if (configuration.getDeleted()) {
            throw new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio");
        }

        if (dto.getAmount() != null) {
            configuration.setAmount(dto.getAmount());
        }

        if (dto.getActive() != null) {
            configuration.setActive(dto.getActive());
        }

        ServiceFeeConfiguration updated = repository.save(configuration);

        return mapResponse(updated);
    }

    @Override
    public ServiceFeeConfigurationResponseDTO getById(UUID id) {

        ServiceFeeConfiguration configuration = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio"));

        if (configuration.getDeleted()) {
            throw new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio");
        }

        return mapResponse(configuration);
    }

    @Override
    public Page<ServiceFeeConfigurationResponseDTO> getAll(
            UUID zoneId,
            ServiceFeeType feeType,
            Boolean active,
            Pageable pageable) {

        System.out.println("feeType: " + feeType);
        return repository.findAll(ServiceFeeConfigurationSpecification.search(zoneId, feeType, active), pageable)
                .map(this::mapResponse);
    }

    @Override
    public void disable(UUID id) {

        ServiceFeeConfiguration configuration = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio"));

        if (configuration.getDeleted()) {
            throw new ResourceNotFoundException("No se ha encontrado la configuración de la tarifa de servicio");
        }

        configuration.setActive(false);

        repository.save(configuration);
    }

    private ServiceFeeConfigurationResponseDTO mapResponse(ServiceFeeConfiguration configuration) {

        return ServiceFeeConfigurationResponseDTO.builder()
                .id(configuration.getId())
                .zoneId(configuration.getZone().getId())
                .zoneName(configuration.getZone().getName())
                .feeType(configuration.getFeeType())
                .amount(configuration.getAmount())
                .active(configuration.getActive())
                .build();
    }
}