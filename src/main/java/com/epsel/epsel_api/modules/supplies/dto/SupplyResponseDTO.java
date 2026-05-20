package com.epsel.epsel_api.modules.supplies.dto;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupplyResponseDTO {

    private UUID id;
    private String supplyNumber;
    private String meterNumber;
    private String internalReference;
    private SupplyStatus status;
    private String customerName;
    private String customerDocument;
    private UUID propertyId;
    private String propertyAddress;
    private String zoneName;
    private PropertyType supplyType;
    private Integer lastReading;
    private LocalDate installationDate;
    private LocalDate activationDate;

}