package com.epsel.epsel_api.modules.users.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkerKpisDTO {
    private long totalActiveWorkers;
    private long activeWorkersChange;
    private long activeSessionsToday;
    private double activeSessionsPercentage;
    private long completedTasks;
    private double completedTasksChange;
    private long pendingIncidents;
    private long criticalAlerts;
}
