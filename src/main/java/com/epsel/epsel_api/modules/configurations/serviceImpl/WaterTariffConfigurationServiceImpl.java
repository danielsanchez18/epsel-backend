package com.epsel.epsel_api.modules.configurations.serviceImpl;

import com.epsel.epsel_api.modules.configurations.dto.CreateWaterTariffConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.WaterTariffConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.configurations.repositories.WaterTariffConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.service.WaterTariffConfigurationService;
import com.epsel.epsel_api.modules.configurations.specifications.WaterTariffConfigurationSpecification;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaterTariffConfigurationServiceImpl implements WaterTariffConfigurationService {

    private final WaterTariffConfigurationRepository repository;
    private final ServiceZoneRepository zoneRepository;

    @Override
    public WaterTariffConfigurationResponseDTO create(CreateWaterTariffConfigurationDTO dto) {

        ServiceZone zone = zoneRepository.findById(dto.getZoneId())
                        .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        if (!zone.getActive()) {
            throw new BadRequestException("La zona no está activa");
        }

        if (dto.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha de vigencia no puede ser anterior a la fecha actual");
        }

        WaterTariffConfiguration tariff = new WaterTariffConfiguration();

        tariff.setZone(zone);
        tariff.setPricePerM3(dto.getPricePerM3());
        tariff.setFixedCharge(dto.getFixedCharge());
        tariff.setTaxPercentage(dto.getTaxPercentage());
        tariff.setEffectiveDate(dto.getEffectiveDate());
        tariff.setActive(true);

        WaterTariffConfiguration saved = repository.save(tariff);

        return mapResponse(saved);
    }

    @Override
    public WaterTariffConfigurationResponseDTO getById(UUID id) {

        WaterTariffConfiguration tariff = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada"));

        if (tariff.getDeleted()) {
            throw new ResourceNotFoundException("Tarifa no encontrada");
        }

        return mapResponse(tariff);
    }

    @Override
    public Page<WaterTariffConfigurationResponseDTO> getAll(
            String zoneName,
            Boolean active,
            Pageable pageable) {

        // Normalizar zoneName
        if (zoneName != null) {
            zoneName = zoneName.trim();
            if (zoneName.isBlank()) {
                zoneName = null;
            }
        }

        Page<WaterTariffConfiguration> page = repository.findAll(
                WaterTariffConfigurationSpecification.search(zoneName, active), pageable);

        // Depuración rápida
        System.out.println("WaterTariffConfigurationServiceImpl.getAll -> zoneName='" + zoneName + "', active='" + active + "', total=" + page.getTotalElements());
        page.forEach(t -> System.out.println("Tariff -> id: " + t.getId() + ", zoneName: '" + t.getZone().getName() + "', active: " + t.getActive()));

        return page.map(this::mapResponse);
    }

    @Override
    public void disable(UUID id) {

        WaterTariffConfiguration tariff = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada"));

        if (tariff.getDeleted()) {
            throw new ResourceNotFoundException("Tarifa no encontrada");
        }

        tariff.setActive(false);

        repository.save(tariff);
    }

    private WaterTariffConfigurationResponseDTO mapResponse(WaterTariffConfiguration tariff) {

        return WaterTariffConfigurationResponseDTO.builder()
                .id(tariff.getId())
                .zoneId(tariff.getZone().getId())
                .zoneName(tariff.getZone().getName())
                .pricePerM3(tariff.getPricePerM3())
                .fixedCharge(tariff.getFixedCharge())
                .taxPercentage(tariff.getTaxPercentage())
                .effectiveDate(tariff.getEffectiveDate())
                .active(tariff.getActive())
                .build();
    }
}