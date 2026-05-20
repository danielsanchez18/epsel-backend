package com.epsel.epsel_api.modules.configurations.serviceImpl;

import com.epsel.epsel_api.modules.configurations.dto.CreateServiceZoneDTO;
import com.epsel.epsel_api.modules.configurations.dto.ServiceZoneResponseDTO;
import com.epsel.epsel_api.modules.configurations.dto.UpdateServiceZoneDTO;
import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.configurations.service.ServiceZoneService;
import com.epsel.epsel_api.modules.configurations.specifications.ServiceZoneSpecification;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceZoneServiceImpl implements ServiceZoneService {

    private final ServiceZoneRepository repository;

    @Override
    public ServiceZoneResponseDTO create(CreateServiceZoneDTO dto) {

        repository.findByNameIgnoreCase(dto.getName()).ifPresent(zone -> {
            if (!zone.getDeleted()) {
                throw new BadRequestException("El nombre de la zona ya existe");
            }
        });

        ServiceZone zone = new ServiceZone();

        zone.setName(dto.getName().trim());
        zone.setDescription(dto.getDescription().trim());
        zone.setActive(true);

        ServiceZone saved = repository.save(zone);

        return mapResponse(saved);
    }

    @Override
    public ServiceZoneResponseDTO update(UUID id, UpdateServiceZoneDTO dto) {

        ServiceZone zone = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona encontrada");
        }

        repository.findByNameIgnoreCase(dto.getName()).ifPresent(existing -> {
            boolean sameName = existing.getName().equalsIgnoreCase(zone.getName());
            if (!sameName && !existing.getDeleted()) {
                throw new BadRequestException("El nombre de la zona ya existe");
            }
        });

        zone.setName(dto.getName().trim());
        zone.setDescription(dto.getDescription().trim());

        if (dto.getActive() != null) {
            zone.setActive(dto.getActive());
        }

        ServiceZone updated = repository.save(zone);

        return mapResponse(updated);
    }

    @Override
    public ServiceZoneResponseDTO getById(UUID id) {

        ServiceZone zone = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        return mapResponse(zone);
    }

    @Override
    public Page<ServiceZoneResponseDTO> getAll(
            String search,
            Boolean active,
            Pageable pageable
    ) {
        Page<ServiceZone> zones = repository.findAll(ServiceZoneSpecification.search(search, active), pageable);
        return zones.map(this::mapResponse);
    }

    @Override
    public void changeStatus(UUID id, Boolean active) {

        ServiceZone zone = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        zone.setActive(active);

        repository.save(zone);
    }

    @Override
    public void delete(UUID id) {

        ServiceZone zone = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Zona no encontrada"));

        if (zone.getDeleted()) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }

        zone.setDeleted(true);
        zone.setActive(false);

        repository.save(zone);
    }

    private ServiceZoneResponseDTO mapResponse(ServiceZone zone) {

        return ServiceZoneResponseDTO.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .active(zone.getActive())
                .build();
    }
}