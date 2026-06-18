package com.epsel.epsel_api.modules.supplies.repositories;

import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstallationRequestRepository
        extends JpaRepository<InstallationRequest, UUID>,
        JpaSpecificationExecutor<InstallationRequest> {

    Boolean existsByPropertyAndStatusIn(Property property, List<InstallationRequestStatus> statuses );

    Optional<InstallationRequest> findByIdAndDeletedFalse(UUID id);

    Boolean existsByPropertyAndInternalReferenceIgnoreCaseAndStatusIn(Property property, String internalReference, List<InstallationRequestStatus> pending);

    Boolean existsByPropertyAndInternalReferenceAndStatusIn(Property property, String internalReference, List<InstallationRequestStatus> pending);

    long countByStatusAndDeletedFalse(InstallationRequestStatus status);

    long countByDeletedFalseAndCreatedAtAfter(java.time.LocalDateTime dateTime);

    long countByStatusAndDeletedFalseAndInstallationDate(InstallationRequestStatus status, java.time.LocalDate date);

    long countByStatusAndDeletedFalseAndRejectedDateAfter(InstallationRequestStatus status, java.time.LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(ir.installationCost), 0) FROM InstallationRequest ir WHERE ir.deleted = false AND ir.createdAt >= :dateTime")
    java.math.BigDecimal sumProjectedRevenueAfter(java.time.LocalDateTime dateTime);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InstallationRequest i WHERE i.deleted = false AND i.status = :status AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    long countByStatusAndDateRange(@org.springframework.data.repository.query.Param("status") InstallationRequestStatus status, @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(i.installationCost), 0) FROM InstallationRequest i WHERE i.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR i.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR i.createdAt <= :endDate)")
    java.math.BigDecimal sumProjectedRevenueByDateRange(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}
