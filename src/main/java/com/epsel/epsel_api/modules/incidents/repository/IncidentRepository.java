package com.epsel.epsel_api.modules.incidents.repository;

import com.epsel.epsel_api.modules.incidents.entity.Incident;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface IncidentRepository extends
        JpaRepository<Incident, UUID>,
        JpaSpecificationExecutor<Incident> {

    boolean existsByIncidentNumber(String incidentNumber);

    long countByDeletedFalse();

    long countByStatusInAndDeletedFalse(List<IncidentStatus> statuses);

    long countByPriorityAndStatusInAndDeletedFalse(IncidentPriority priority, List<IncidentStatus> statuses);

    long countByCustomerIdAndStatusInAndDeletedFalse(UUID customerId, List<IncidentStatus> statuses);

    long countByCustomerIdAndDeletedFalseAndReportedDateAfter(UUID customerId, java.time.LocalDate date);
}