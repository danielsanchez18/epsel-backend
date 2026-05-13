package com.epsel.epsel_api.modules.payments.services;

import com.epsel.epsel_api.modules.payments.dto.CreatePaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponseDTO create(CreatePaymentDTO dto);

    PaymentResponseDTO getById(UUID id);

    Page<PaymentResponseDTO> getByBilling(UUID billingId, Pageable pageable);

}