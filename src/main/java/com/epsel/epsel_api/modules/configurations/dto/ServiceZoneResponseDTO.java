package com.epsel.epsel_api.modules.configurations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ServiceZoneResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private Boolean active;

}