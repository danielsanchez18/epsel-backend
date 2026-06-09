package com.epsel.epsel_api.modules.billing.repositories;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.projection.MonthlyBillingProjection;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillingRepository extends
        JpaRepository<Billing, UUID>,
        JpaSpecificationExecutor<Billing> {

    boolean existsByReading(MeterReading reading);

    Optional<Billing> findByReading(MeterReading reading);

    long countBySupplyAndDeletedFalse(Supply supply);

    Page<Billing> findBySupplyIdAndDeletedFalse(UUID supplyId, Pageable pageable);

    long countByStatusAndDeletedFalse(BillingStatus status);

    @Query("SELECT COUNT(b) FROM Billing b WHERE b.deleted = false AND (b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE OR (b.status IN (com.epsel.epsel_api.modules.billing.enums.BillingStatus.PENDING, com.epsel.epsel_api.modules.billing.enums.BillingStatus.PARTIALLY_PAID) AND b.dueDate < CURRENT_DATE))")
    long countRealOverdueBills();

    @Query("""
        SELECT COALESCE(SUM(b.totalAmount),0)
        FROM Billing b
        WHERE MONTH(b.billingDate)=:month
        AND YEAR(b.billingDate)=:year
        AND b.deleted=false
    """)
    BigDecimal getTotalBilledMonth(
            Integer month,
            Integer year
    );

    @Query("""
        SELECT COALESCE(SUM(b.pendingAmount),0)
        FROM Billing b
        WHERE b.deleted=false
        AND b.status <> 'PAID'
    """)
    BigDecimal getTotalPendingCollection();

    @Query("""
        SELECT
            MONTH(b.billingDate) as month,
            SUM(b.totalAmount) as total
        FROM Billing b
        WHERE YEAR(b.billingDate) = :year
        AND b.deleted = false
        GROUP BY MONTH(b.billingDate)
        ORDER BY MONTH(b.billingDate)
    """)
    List<MonthlyBillingProjection> getBillingByMonth(Integer year);

    @Query("""
        SELECT
            MONTH(b.billingDate) as month,
            COALESCE(SUM(b.consumption), 0) as total
        FROM Billing b
        WHERE YEAR(b.billingDate) = :year
        AND b.deleted = false
        GROUP BY MONTH(b.billingDate)
        ORDER BY MONTH(b.billingDate)
    """)
    List<MonthlyBillingProjection> getConsumptionByMonth(Integer year);

    @Query("SELECT COUNT(b) FROM Billing b WHERE MONTH(b.createdAt) = :month AND YEAR(b.createdAt) = :year AND b.status = :status AND b.deleted = false")
    long countByStatusAndCreatedAtMonthAndYear(
            @Param("status") BillingStatus status,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT COALESCE(SUM(b.pendingAmount),0)
        FROM Billing b
        WHERE MONTH(b.billingDate)=:month
        AND YEAR(b.billingDate)=:year
        AND b.deleted=false
        AND b.status <> 'PAID'
    """)
    java.math.BigDecimal getTotalPendingBilledMonth(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    List<Billing> findByDeletedFalse(Pageable pageable);

    @Query("SELECT COUNT(DISTINCT b.supply.customer) FROM Billing b WHERE b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE AND b.deleted = false")
    long countDelinquentCustomers();

    @Query("SELECT COALESCE(SUM(b.pendingAmount), 0) FROM Billing b WHERE b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE AND b.deleted = false")
    BigDecimal sumDelinquentAmount();

    @Query("SELECT COALESCE(SUM(b.pendingAmount), 0) FROM Billing b WHERE b.supply.customer.id = :customerId AND b.status <> com.epsel.epsel_api.modules.billing.enums.BillingStatus.PAID AND b.deleted = false")
    BigDecimal sumPendingAmountByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(b) FROM Billing b WHERE b.supply.customer.id = :customerId AND b.status = com.epsel.epsel_api.modules.billing.enums.BillingStatus.OVERDUE AND b.deleted = false")
    long countOverdueBillsByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COALESCE(AVG(b.consumption), 0.0) FROM Billing b WHERE b.supply.customer.id = :customerId AND b.deleted = false")
    double averageConsumptionByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT b.consumption FROM Billing b WHERE b.supply.customer.id = :customerId AND b.deleted = false ORDER BY b.billingYear DESC, b.billingMonth DESC")
    List<Integer> findConsumptionsByCustomerIdOrderByDateDesc(@Param("customerId") UUID customerId, org.springframework.data.domain.Pageable pageable);
}