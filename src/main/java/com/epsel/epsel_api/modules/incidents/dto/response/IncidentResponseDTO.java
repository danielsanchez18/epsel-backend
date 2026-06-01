package com.epsel.epsel_api.modules.incidents.dto.response;

import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class IncidentResponseDTO {

    private UUID id;

    private String incidentNumber;

    private UUID customerId;
    private String customerName;

    private UUID propertyId;

    private UUID supplyId;
    private String supplyNumber;

    private IncidentType type;

    private IncidentPriority priority;

    private IncidentStatus status;

    private String title;

    private String description;

    private LocalDate reportedDate;

    private LocalDate resolvedDate;

    private String resolution;

}
