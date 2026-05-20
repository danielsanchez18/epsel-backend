package com.epsel.epsel_api.modules.supplies.dto;

import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InstallationRequestResponseDTO {

    private UUID id;
    private UUID customerId;
    private String customerName;
    private String zoneName;
    private UUID propertyId;
    private String propertyAddress;
    private BigDecimal installationCost;
    private InstallationRequestStatus status;
    private LocalDate requestedDate;
    private LocalDate approvedDate;
    private LocalDate installationDate;
    private LocalDate rejectedDate;
    private String approvedBy;
    private String installedBy;
    private String rejectedBy;
    private String observations;

}
