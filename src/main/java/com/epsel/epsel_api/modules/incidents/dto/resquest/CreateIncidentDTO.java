package com.epsel.epsel_api.modules.incidents.dto.resquest;

import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateIncidentDTO {

    private UUID customerId;

    private UUID propertyId;

    private UUID supplyId;

    @NotNull
    private IncidentType type;

    @NotNull
    private IncidentPriority priority;

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}