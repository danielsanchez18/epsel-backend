package com.epsel.epsel_api.modules.billing.servicesImpl;

import com.epsel.epsel_api.modules.billing.dto.BillingResponseDTO;
import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.billing.services.BillingService;
import com.epsel.epsel_api.modules.configurations.entities.BillingConfiguration;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import com.epsel.epsel_api.modules.configurations.repositories.BillingConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.repositories.WaterTariffConfigurationRepository;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

        if (repository.existsByReading(reading)) {
            throw new BadRequestException("Ya existe una factura correspondiente a esta lectura");
        }

        Supply supply = reading.getSupply();

        WaterTariffConfiguration tariff = tariffRepository
                        .findFirstByZoneAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                                supply.getProperty().getZone(),
                                LocalDate.now()
                        ).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado ninguna configuración de tarifa activa"));

        BillingConfiguration billingConfig = billingConfigurationRepository
                        .findFirstByActiveTrue()
                        .orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado la configuración de facturación"));

        BigDecimal consumption = BigDecimal.valueOf(reading.getConsumption());
        BigDecimal subtotalConsumption = consumption.multiply(tariff.getPricePerM3());
        BigDecimal subtotal = subtotalConsumption.add(tariff.getFixedCharge());

        BigDecimal taxAmount = subtotal.multiply(
                        tariff.getTaxPercentage().divide(
                                BigDecimal.valueOf(100),
                                4,
                                RoundingMode.HALF_UP)
        );

        BigDecimal total = subtotal.add(taxAmount);

        Billing billing = new Billing();

        billing.setSupply(supply);
        billing.setReading(reading);
        billing.setConsumption(reading.getConsumption());
        billing.setUnitPrice(tariff.getPricePerM3());
        billing.setFixedCharge(tariff.getFixedCharge());
        billing.setSubtotal(subtotal);
        billing.setTaxAmount(taxAmount);
        billing.setTotalAmount(total);
        billing.setBillingDate(LocalDate.now());
        billing.setDueDate(LocalDate.now().plusDays(billingConfig.getGraceDays()));
        billing.setStatus(BillingStatus.PENDING);

        Billing saved = repository.save(billing);

        reading.setStatus(ReadingStatus.BILLED);

        readingRepository.save(reading);
        return mapResponse(saved);
    }

    @Override
    public BillingResponseDTO getById(UUID id) {
        Billing billing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        return mapResponse(billing);
    }

    @Override
    public Page<BillingResponseDTO> getBySupply(UUID supplyId, Pageable pageable) {
        return repository.findBySupplyIdAndDeletedFalse(supplyId, pageable)
                .map(this::mapResponse);
    }

    private BillingResponseDTO mapResponse(Billing billing) {
        return BillingResponseDTO.builder()
                .id(billing.getId())
                .supplyId(billing.getSupply().getId())
                .supplyNumber(billing.getSupply().getSupplyNumber())
                .readingId(billing.getReading().getId())
                .consumption(billing.getConsumption())
                .unitPrice(billing.getUnitPrice())
                .fixedCharge(billing.getFixedCharge())
                .subtotal(billing.getSubtotal())
                .taxAmount(billing.getTaxAmount())
                .totalAmount(billing.getTotalAmount())
                .billingDate(billing.getBillingDate())
                .dueDate(billing.getDueDate())
                .status(billing.getStatus())
                .build();
    }
}