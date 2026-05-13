package com.epsel.epsel_api.modules.billing.services;

import com.epsel.epsel_api.modules.billing.dto.BillingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BillingService {

    BillingResponseDTO generate(UUID readingId);

    BillingResponseDTO getById(UUID id);

    Page<BillingResponseDTO> getBySupply(UUID supplyId, Pageable pageable);

}