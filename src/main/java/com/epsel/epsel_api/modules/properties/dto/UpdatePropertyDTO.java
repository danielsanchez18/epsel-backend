package com.epsel.epsel_api.modules.properties.dto;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePropertyDTO {

    private PropertyType type;
    private String address;
    private String reference;
    private String cadastralCode;
    private Double latitude;
    private Double longitude;
    private UUID zoneId;

}