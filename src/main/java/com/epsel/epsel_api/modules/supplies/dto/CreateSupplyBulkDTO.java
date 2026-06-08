package com.epsel.epsel_api.modules.supplies.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateSupplyBulkDTO {
    private UUID customerId;
    private UUID propertyId;
    private String meterNumber;
    private String internalReference;
    private LocalDate installationDate;
    private Integer lastReading;
}
