package com.epsel.epsel_api.modules.dashboard.service;

import com.epsel.epsel_api.modules.dashboard.dto.DashboardResponseDTO;

public interface DashboardService {

    DashboardResponseDTO getDashboard(Integer month, Integer year);

}
