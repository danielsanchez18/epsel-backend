package com.epsel.epsel_api.modules.readings.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadingKpisDTO {
    private long registeredToday;
    private long pending;
    private long validated;
    private long billed;
    private long cancelled;
    private long monthConsumption;
}
