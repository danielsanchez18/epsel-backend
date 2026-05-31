package com.epsel.epsel_api.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardAlertDTO {

    private String title;

    private String description;

    private String severity;
}