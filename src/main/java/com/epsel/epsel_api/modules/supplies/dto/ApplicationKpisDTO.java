package com.epsel.epsel_api.modules.supplies.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class ApplicationKpisDTO {
    private long pendingApplications;
    private long applicationsChangeThisMonth;
    private long approvedApplications;
    private long installedToday;
    private long rejectedApplications;
    private long rejectedApplicationsChangeThisMonth;
    private BigDecimal projectedRevenue;
}
