package com.epsel.epsel_api.modules.properties.dto;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PropertyResponseDTO {

    private UUID id;
    private UUID customerId;
    private String customerName;
    private PropertyType type;
    private String cadastralCode;
    private String address;
    private Double latitude;
    private Double longitude;
    private String reference;
    private UUID zoneId;
    private String zoneName;

}