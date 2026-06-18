package com.epsel.epsel_api.modules.incidents.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentKpiDTO {
    private long total;
    private long open;
    private long inProgress;
    private long resolved;
}
