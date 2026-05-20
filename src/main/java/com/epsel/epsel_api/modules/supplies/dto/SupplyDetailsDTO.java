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
public class SupplyDetailsDTO {

    private UUID id;
    private String supplyNumber;
    private String meterNumber;
    private String internalReference;
    private SupplyStatus status;
    private String customerName;
    private String customerDocument;
    private String customerPhone;
    private String propertyAddress;
    private String propertyReference;
    private String cadastralCode;
    private String zoneName;
    private PropertyType supplyType;
    private Integer lastReading;
    private Double latitude;
    private Double longitude;
    private LocalDate installationDate;
    private LocalDate activationDate;
    private LocalDate cutOffDate;
    private LocalDate reconnectionDate;
    private String cutOffReason;
    private LocalDate createdAt;

}
