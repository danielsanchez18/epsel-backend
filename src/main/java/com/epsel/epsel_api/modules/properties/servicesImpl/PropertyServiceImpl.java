package com.epsel.epsel_api.modules.properties.servicesImpl;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.dto.CreatePropertyDTO;
import com.epsel.epsel_api.modules.properties.dto.PropertyResponseDTO;
import com.epsel.epsel_api.modules.properties.dto.UpdatePropertyDTO;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.properties.services.PropertyService;
import com.epsel.epsel_api.modules.properties.specifications.PropertySpecification;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository repository;
    private final CustomerRepository customerRepository;
    private final ServiceZoneRepository zoneRepository;

    @Override
    public PropertyResponseDTO create(CreatePropertyDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        ServiceZone zone = zoneRepository.findById(dto.getZoneId())
                        .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));

        Property property = new Property();

        property.setCustomer(customer);
        property.setType(dto.getType());
        property.setAddress(dto.getAddress());
        property.setReference(dto.getReference());
        property.setZone(zone);

        Property saved = repository.save(property);
        return mapResponse(saved);
    }

    @Override
    public PropertyResponseDTO update(UUID id, UpdatePropertyDTO dto) {

        Property property = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        if (dto.getType() != null) { property.setType(dto.getType()); }
        if (dto.getAddress() != null) {property.setAddress(dto.getAddress()); }
        if (dto.getReference() != null) { property.setReference(dto.getReference()); }

        if (dto.getZoneId() != null) {
            ServiceZone zone = zoneRepository.findById(dto.getZoneId()).orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));
            property.setZone(zone);
        }

        Property saved = repository.save(property);
        return mapResponse(saved);
    }

    @Override
    public PropertyResponseDTO getById(UUID id) {
        Property property = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        return mapResponse(property);
    }

    @Override
    public Page<PropertyResponseDTO> search(String search, PropertyType type, UUID customerId, Pageable pageable) {

        return repository.findAll(
                PropertySpecification.search(
                        search,
                        type,
                        customerId),
                pageable).map(this::mapResponse);
    }

    @Override
    public void delete(UUID id) {

        Property property = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        property.setDeleted(true);
        repository.save(property);
    }

    private PropertyResponseDTO mapResponse(Property property) {

        return PropertyResponseDTO.builder()
                .id(property.getId())
                .customerId(property.getCustomer().getId())
                .customerName(property.getCustomer().getFullName())
                .type(property.getType())
                .address(property.getAddress())
                .reference(property.getReference())
                .zoneId(property.getZone().getId())
                .zoneName(property.getZone().getName().name())
                .build();
    }
}