package com.epsel.epsel_api.modules.billing.services;

import com.epsel.epsel_api.modules.billing.dto.BillingResponseDTO;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface BillingService {

    BillingResponseDTO generate(UUID readingId);

    BillingResponseDTO getById(UUID id);

    Page<BillingResponseDTO> getBySupply(UUID supplyId, Pageable pageable);

    Page<BillingResponseDTO> search(
            String billingNumber,
            String customerName,
            java.util.List<BillingStatus> status,
            LocalDate startDate,
            LocalDate endDate,
            Boolean overdue,
            Pageable pageable
    );
}