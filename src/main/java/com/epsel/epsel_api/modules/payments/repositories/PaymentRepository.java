package com.epsel.epsel_api.modules.payments.repositories;

import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.payments.projections.MonthlyPaymentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, UUID>,
        JpaSpecificationExecutor<Payment> {

    // BigDecimal sumByBillingIdAndStatus(UUID billingId, PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.billing.id = :billingId AND p.status = :status")
    BigDecimal sumByBillingIdAndStatus(
            @Param("billingId") UUID billingId,
            @Param("status") PaymentStatus status
    );


    Page<Payment> findByBillingIdAndDeletedFalse(UUID billingId, Pageable pageable);

    Boolean existsByOperationNumber(String operationNumber);

    @Query("""
        SELECT COALESCE(SUM(p.amount),0)
        FROM Payment p
        WHERE p.status='COMPLETED'
        AND MONTH(p.paymentDate)=:month
        AND YEAR(p.paymentDate)=:year
        AND p.deleted=false
    """)
    BigDecimal getTotalCollectedMonth(
            Integer month,
            Integer year
    );

    @Query("""
        SELECT
            MONTH(p.paymentDate) as month,
            SUM(p.amount) as total
        FROM Payment p
        WHERE YEAR(p.paymentDate) = :year
        AND p.deleted = false
        AND p.status = 'COMPLETED'
        GROUP BY MONTH(p.paymentDate)
        ORDER BY MONTH(p.paymentDate)
    """)
    List<MonthlyPaymentProjection> getPaymentsByMonth(Integer year);

    List<Payment> findByDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.billing.supply.customer.id = :customerId AND p.status = com.epsel.epsel_api.modules.payments.enums.PaymentStatus.COMPLETED AND p.deleted = false ORDER BY p.paymentDate DESC")
    List<Payment> findLatestCompletedPaymentByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.billing.supply.customer.id = :customerId AND p.deleted = false")
    org.springframework.data.domain.Page<Payment> findByCustomerIdAndDeletedFalse(@Param("customerId") UUID customerId, Pageable pageable);

    // KPIs

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.deleted = false AND DATE(p.paymentDate) = CURRENT_DATE")
    BigDecimal sumCompletedPaymentsToday();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.deleted = false AND (CAST(:startDate AS timestamp) IS NULL OR p.paymentDate >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR p.paymentDate <= :endDate)")
    BigDecimal sumCompletedPaymentsBetweenDates(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.deleted = false AND p.paymentMethod = :method AND (CAST(:startDate AS timestamp) IS NULL OR p.paymentDate >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR p.paymentDate <= :endDate)")
    BigDecimal sumCompletedPaymentsByMethodBetweenDates(@Param("method") com.epsel.epsel_api.modules.payments.enums.PaymentMethod method, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.deleted = false AND p.paymentMethod IN :methods AND (CAST(:startDate AS timestamp) IS NULL OR p.paymentDate >= :startDate) AND (CAST(:endDate AS timestamp) IS NULL OR p.paymentDate <= :endDate)")
    BigDecimal sumCompletedPaymentsByMethodsBetweenDates(@Param("methods") List<com.epsel.epsel_api.modules.payments.enums.PaymentMethod> methods, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
