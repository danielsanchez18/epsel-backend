package com.epsel.epsel_api.modules.payments.repositories;

import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

}