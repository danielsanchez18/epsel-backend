package com.epsel.epsel_api.modules.incidents.dto.resquest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveIncidentDTO {

    @NotBlank
    private String resolution;
}
