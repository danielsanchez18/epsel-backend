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

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Incident i WHERE i.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    long countTotalForDashboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Incident i WHERE i.deleted = false AND i.status = com.epsel.epsel_api.modules.incidents.enums.IncidentStatus.OPEN AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    long countOpenForDashboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Incident i WHERE i.deleted = false AND i.status = com.epsel.epsel_api.modules.incidents.enums.IncidentStatus.IN_PROGRESS AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    long countInProgressForDashboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Incident i WHERE i.deleted = false AND i.status = com.epsel.epsel_api.modules.incidents.enums.IncidentStatus.RESOLVED AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    long countResolvedForDashboard(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}