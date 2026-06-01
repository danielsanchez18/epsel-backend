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

}