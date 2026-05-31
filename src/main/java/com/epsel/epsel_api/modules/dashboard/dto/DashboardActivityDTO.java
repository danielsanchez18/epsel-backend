package com.epsel.epsel_api.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DashboardActivityDTO {

    private String action;

    private String description;

    private LocalDateTime date;

}