package com.epsel.epsel_api.modules.billing.servicesImpl;

import com.epsel.epsel_api.modules.billing.dto.BillingResponseDTO;
import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.billing.services.BillingService;
import com.epsel.epsel_api.modules.billing.specifications.BillingSpecification;
import com.epsel.epsel_api.modules.configurations.entities.BillingConfiguration;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import com.epsel.epsel_api.modules.configurations.repositories.BillingConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.repositories.WaterTariffConfigurationRepository;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillingRepository repository;
    private final MeterReadingRepository readingRepository;
    private final WaterTariffConfigurationRepository tariffRepository;
    private final BillingConfigurationRepository billingConfigurationRepository;

    @Override
    public BillingResponseDTO generate(UUID readingId) {

        MeterReading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Lectura no encontrada"));

        if (reading.getDeleted()) {
            throw new ResourceNotFoundException("Lectura no encontrada");
        }

        if (reading.getStatus() != ReadingStatus.VALIDATED) {
            throw new BadRequestException("La lectura debe estar validada para generar una factura");
        }

        if (reading.getConsumption() <= 0) {
            throw new BadRequestException("El consumo debe ser mayor a cero");
        }

        if (repository.existsByReading(reading)) {
            throw new BadRequestException("Ya existe una factura para esta lectura");
        }

        Supply supply = reading.getSupply();

        if (supply.getDeleted()) {
            throw new ResourceNotFoundException("Suministro no encontrado");
        }

        if (supply.getStatus() != SupplyStatus.ACTIVE) {
            throw new BadRequestException("El suministro no está activo");
        }

        WaterTariffConfiguration tariff = tariffRepository
                .findFirstByZoneAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        supply.getProperty().getZone(),
                        reading.getReadingDate()
                )
                .orElseThrow(() -> new ResourceNotFoundException("No existe una tarifa vigente"));

        BillingConfiguration billingConfig =
                billingConfigurationRepository
                        .findFirstByActiveTrue()
                        .orElseThrow(() -> new ResourceNotFoundException("Configuración de facturación no encontrada"));

        BigDecimal consumption = BigDecimal.valueOf(reading.getConsumption());

        BigDecimal subtotalConsumption = consumption.multiply(tariff.getPricePerM3());

        BigDecimal subtotal = subtotalConsumption
                        .add(tariff.getFixedCharge())
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = subtotal.multiply(
                                tariff.getTaxPercentage().divide(
                                        BigDecimal.valueOf(100),
                                        4,
                                        RoundingMode.HALF_UP))
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        LocalDate billingDate = LocalDate.now();

        Billing billing = new Billing();

        billing.setBillingNumber(generateBillingNumber());
        billing.setSupply(supply);
        billing.setReading(reading);
        billing.setBillingMonth(reading.getReadingDate().getMonthValue());
        billing.setBillingYear(reading.getReadingDate().getYear());
        billing.setConsumption(reading.getConsumption());
        billing.setUnitPrice(tariff.getPricePerM3());
        billing.setFixedCharge(tariff.getFixedCharge());
        billing.setTaxPercentage(tariff.getTaxPercentage());
        billing.setSubtotal(subtotal);
        billing.setTaxAmount(taxAmount);
        billing.setLateFeeAmount(BigDecimal.ZERO);
        billing.setTotalAmount(total);
        billing.setAmountPaid(BigDecimal.ZERO);
        billing.setPendingAmount(total);
        billing.setBillingDate(billingDate);
        billing.setDueDate(billingDate.plusDays(billingConfig.getGraceDays()));
        billing.setStatus(BillingStatus.PENDING);
        billing.setPrinted(false);

        Billing saved = repository.save(billing);

        reading.setStatus(ReadingStatus.BILLED);

        readingRepository.save(reading);

        return mapResponse(saved);
    }

    @Override
    public BillingResponseDTO getById(UUID id) {

        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

        if (billing.getDeleted()) {
            throw new ResourceNotFoundException("Factura no encontrada");
        }

        return mapResponse(billing);
    }

    @Override
    public com.epsel.epsel_api.modules.billing.dto.BillingKpiDTO getKpis(LocalDateTime startDate, LocalDateTime endDate) {
        long pendingCount = repository.countBillsByStatusForDashboard(BillingStatus.PENDING, startDate, endDate);
        long overdueCount = repository.countBillsByStatusForDashboard(BillingStatus.OVERDUE, startDate, endDate);
        long paidCount = repository.countBillsByStatusForDashboard(BillingStatus.PAID, startDate, endDate);
        BigDecimal totalCollected = repository.sumTotalCollectedForDashboard(startDate, endDate);
        BigDecimal totalPending = repository.sumTotalPendingForDashboard(startDate, endDate);

        return com.epsel.epsel_api.modules.billing.dto.BillingKpiDTO.builder()
                .pendingCount(pendingCount)
                .overdueCount(overdueCount)
                .paidCount(paidCount)
                .totalCollected(totalCollected)
                .totalPending(totalPending)
                .build();
    }

    @Override
    public Page<BillingResponseDTO> search(
            String billingNumber,
            String customerName,
            java.util.List<BillingStatus> status,
            LocalDate startDate,
            LocalDate endDate,
            Boolean overdue,
            Pageable pageable
    ) {

        return repository.findAll(BillingSpecification.search(
                                billingNumber,
                                customerName,
                                status,
                                startDate,
                                endDate,
                                overdue), pageable).map(this::mapResponse);
    }

    @Override
    public Page<BillingResponseDTO> getBySupply(UUID supplyId, Pageable pageable) {

        return repository.findBySupplyIdAndDeletedFalse(supplyId, pageable)
                .map(this::mapResponse);
    }

    private String generateBillingNumber() {
        long count = repository.count() + 1;

        return String.format(
                "FAC-%d-%06d",
                LocalDate.now().getYear(),
                count
        );
    }

    private BillingResponseDTO mapResponse(Billing billing) {

        return BillingResponseDTO.builder()
                .id(billing.getId())

                .billingNumber(billing.getBillingNumber())

                .supplyId(billing.getSupply().getId())
                .supplyNumber(billing.getSupply().getSupplyNumber())

                .readingId(billing.getReading().getId())

                .customerName(
                        billing.getSupply()
                                .getCustomer()
                                .getFullName()
                )

                .propertyAddress(
                        billing.getSupply()
                                .getProperty()
                                .getAddress()
                )

                .zoneName(
                        billing.getSupply()
                                .getProperty()
                                .getZone()
                                .getName()
                )

                .billingMonth(billing.getBillingMonth())
                .billingYear(billing.getBillingYear())

                .consumption(billing.getConsumption())

                .unitPrice(billing.getUnitPrice())
                .fixedCharge(billing.getFixedCharge())
                .taxPercentage(billing.getTaxPercentage())

                .subtotal(billing.getSubtotal())
                .taxAmount(billing.getTaxAmount())

                .lateFeeAmount(billing.getLateFeeAmount())

                .totalAmount(billing.getTotalAmount())
                .amountPaid(billing.getAmountPaid())
                .pendingAmount(billing.getPendingAmount())

                .billingDate(billing.getBillingDate())
                .dueDate(billing.getDueDate())

                .status(billing.getStatus())

                .printed(billing.getPrinted())

                .build();
    }

}