package com.epsel.epsel_api.modules.configurations.serviceImpl;

import com.epsel.epsel_api.modules.configurations.dto.CreateWaterTariffConfigurationDTO;
import com.epsel.epsel_api.modules.configurations.dto.WaterTariffConfigurationResponseDTO;
import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.TariffStatus;
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
import java.util.List;
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
        tariff.setStatus(TariffStatus.UPCOMING);

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

    private TariffStatus calculateStatus(WaterTariffConfiguration tariff) {
        if (!tariff.getActive()) {
            return TariffStatus.DISABLED;
        }

        LocalDate today = LocalDate.now();

        List<WaterTariffConfiguration> tariffs = repository
                .findByZoneAndActiveTrueOrderByEffectiveDateDesc(
                        tariff.getZone()
                );

        WaterTariffConfiguration current = tariffs.stream()
                .filter(t -> !t.getEffectiveDate().isAfter(today))
                .findFirst()
                .orElse(null);

        if (current != null && current.getId().equals(tariff.getId())) {
            return TariffStatus.ACTIVE;
        }

        if (tariff.getEffectiveDate().isAfter(today)) {
            return TariffStatus.UPCOMING;
        }

        return TariffStatus.HISTORICAL;
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
                .status(calculateStatus(tariff))
                .build();
    }
}