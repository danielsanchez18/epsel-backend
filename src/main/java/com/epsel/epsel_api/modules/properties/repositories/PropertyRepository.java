package com.epsel.epsel_api.modules.properties.repositories;

import com.epsel.epsel_api.modules.properties.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends
        JpaRepository<Property, UUID>,
        JpaSpecificationExecutor<Property> {

    Optional<Property> findByIdAndDeletedFalse(UUID id);

    boolean existsByCadastralCode(String cadastralCode);

    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);

    Optional<Property> findByCadastralCode(String cadastralCode);

    long countByDeletedFalse();

    long countByDeletedFalseAndCreatedAtBefore(java.time.LocalDateTime dateTime);

    @Query("SELECT COUNT(p) FROM Property p WHERE MONTH(p.createdAt) = :month AND YEAR(p.createdAt) = :year AND p.deleted = false")
    long countCreatedInMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR p.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR p.createdAt <= :endDate)")
    long countPropertiesByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT s.property) FROM Supply s WHERE s.status = 'ACTIVE' AND s.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR s.property.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR s.property.createdAt <= :endDate)")
    long countActivePropertiesByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.deleted = false AND NOT EXISTS (SELECT s FROM Supply s WHERE s.property = p AND s.deleted = false) AND (CAST(:startDate AS timestamp) IS NULL OR p.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR p.createdAt <= :endDate)")
    long countPropertiesWithoutSupplyByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT b.supply.property) FROM Billing b WHERE b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE AND b.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR b.supply.property.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR b.supply.property.createdAt <= :endDate)")
    long countCriticalDebtPropertiesByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT b.supply.property) FROM Billing b WHERE b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE AND b.deleted = false AND b.pendingAmount > 1000 AND (CAST(:startDate AS timestamp) IS NULL OR b.supply.property.createdAt >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR b.supply.property.createdAt <= :endDate)")
    long countCriticalDebtPropertiesOver1000ByDateRange(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
