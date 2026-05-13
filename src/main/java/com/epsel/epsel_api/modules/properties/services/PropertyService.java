package com.epsel.epsel_api.modules.properties.services;

import com.epsel.epsel_api.modules.properties.dto.CreatePropertyDTO;
import com.epsel.epsel_api.modules.properties.dto.PropertyResponseDTO;
import com.epsel.epsel_api.modules.properties.dto.UpdatePropertyDTO;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PropertyService {

    PropertyResponseDTO create(CreatePropertyDTO dto);

    PropertyResponseDTO update(UUID id, UpdatePropertyDTO dto);

    PropertyResponseDTO getById(UUID id);

    Page<PropertyResponseDTO> search(
            String search,
            PropertyType type,
            UUID customerId,
            Pageable pageable
    );

    void delete(UUID id);
}
