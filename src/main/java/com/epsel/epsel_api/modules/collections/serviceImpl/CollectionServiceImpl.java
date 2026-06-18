package com.epsel.epsel_api.modules.collections.serviceImpl;

import com.epsel.epsel_api.shared.responses.ApiResponse;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.collections.dto.CollectionKpiDTO;
import com.epsel.epsel_api.modules.collections.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final BillingRepository billingRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CollectionKpiDTO> getKpis(LocalDateTime startDate, LocalDateTime endDate) {

        long pendingCount = billingRepository.countPendingBillsForCollection(startDate, endDate);
        long overdueCount = billingRepository.countOverdueBillsForCollection(startDate, endDate);
        BigDecimal totalPendingAmount = billingRepository.sumPendingAmountForCollection(startDate, endDate);
        BigDecimal totalOverdueAmount = billingRepository.sumOverdueAmountForCollection(startDate, endDate);
        long delinquentCustomersCount = billingRepository.countDelinquentCustomersForCollection(startDate, endDate);
        long suppliesToCutCount = billingRepository.countSuppliesToCutForCollection(startDate, endDate);

        CollectionKpiDTO dto = CollectionKpiDTO.builder()
                .pendingCount(pendingCount)
                .overdueCount(overdueCount)
                .totalPendingAmount(totalPendingAmount)
                .totalOverdueAmount(totalOverdueAmount)
                .delinquentCustomersCount(delinquentCustomersCount)
                .suppliesToCutCount(suppliesToCutCount)
                .build();

        return ApiResponse.success("KPIs de cobranza obtenidos exitosamente", dto);
    }
}
