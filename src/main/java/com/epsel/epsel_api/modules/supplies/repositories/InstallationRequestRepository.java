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
}
