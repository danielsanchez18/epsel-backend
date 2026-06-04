package com.epsel.epsel_api.modules.payments.services;

import com.epsel.epsel_api.modules.payments.dto.CancelPaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.CreatePaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.PaymentResponseDTO;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDTO create(CreatePaymentDTO dto);

    PaymentResponseDTO getById(UUID id);

    Page<PaymentResponseDTO> search(
            String receiptNumber,
            String billingNumber,
            String supplyNumber,
            String customerName,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<PaymentResponseDTO> getByBilling(UUID billingId, Pageable pageable);

    Page<PaymentResponseDTO> getByCustomer(UUID customerId, Pageable pageable);

    PaymentResponseDTO cancel(
            UUID paymentId,
            CancelPaymentDTO dto
    );
}