package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingSeeder {

    private final BillingRepository billingRepository;
    private final MeterReadingRepository readingRepository;

    private final Random random = new Random();

    public void generate() {

        if (billingRepository.count() > 0) {
            return;
        }

        List<MeterReading> readings =
                readingRepository.findAll()
                        .stream()
                        .filter(r ->
                                r.getStatus() ==
                                        ReadingStatus.BILLED
                        )
                        .toList();

        List<Billing> billings =
                new ArrayList<>();

        int sequence = 1;

        for (MeterReading reading : readings) {

            Billing billing =
                    new Billing();

            billing.setBillingNumber(
                    String.format(
                            "FAC-%08d",
                            sequence++
                    )
            );

            billing.setSupply(
                    reading.getSupply()
            );

            billing.setReading(
                    reading
            );

            billing.setBillingMonth(
                    reading.getReadingDate()
                            .getMonthValue()
            );

            billing.setBillingYear(
                    reading.getReadingDate()
                            .getYear()
            );

            billing.setConsumption(
                    reading.getConsumption()
            );

            BigDecimal unitPrice =
                    getUnitPrice(
                            reading.getSupply()
                                    .getSupplyType()
                    );

            BigDecimal fixedCharge =
                    BigDecimal.valueOf(5.00);

            BigDecimal taxPercentage =
                    BigDecimal.valueOf(18);

            BigDecimal subtotal =
                    unitPrice.multiply(
                                    BigDecimal.valueOf(
                                            reading.getConsumption()
                                    )
                            )
                            .add(fixedCharge);

            BigDecimal taxAmount =
                    subtotal.multiply(
                            taxPercentage
                                    .divide(
                                            BigDecimal.valueOf(100),
                                            2,
                                            RoundingMode.HALF_UP
                                    )
                    );

            BigDecimal total =
                    subtotal.add(taxAmount);

            billing.setUnitPrice(unitPrice);
            billing.setFixedCharge(fixedCharge);
            billing.setTaxPercentage(taxPercentage);
            billing.setSubtotal(subtotal);
            billing.setTaxAmount(taxAmount);

            billing.setLateFeeAmount(
                    BigDecimal.ZERO
            );

            billing.setTotalAmount(total);

            BillingStatus status =
                    generateStatus();

            billing.setStatus(status);

            if (status == BillingStatus.PAID) {

                billing.setAmountPaid(total);

                billing.setPendingAmount(
                        BigDecimal.ZERO
                );

                LocalDate paidDate = reading.getReadingDate()
                        .plusDays(
                                random.nextInt(20) + 1
                        );

                billing.setPaidDate(
                        paidDate.isAfter(LocalDate.now())
                                ? LocalDate.now()
                                : paidDate
                );

            }

            else if (
                    status ==
                            BillingStatus.PARTIALLY_PAID
            ) {

                BigDecimal paid =
                        total.multiply(
                                BigDecimal.valueOf(
                                        0.50
                                )
                        );

                billing.setAmountPaid(paid);

                billing.setPendingAmount(
                        total.subtract(paid)
                );

            }

            else {

                billing.setAmountPaid(
                        BigDecimal.ZERO
                );

                billing.setPendingAmount(
                        total
                );

            }

            billing.setBillingDate(
                    reading.getReadingDate()
            );

            billing.setDueDate(
                    reading.getReadingDate()
                            .plusDays(15)
            );

            billing.setPrinted(
                    random.nextBoolean()
            );

            billings.add(billing);
        }

        billingRepository.saveAll(
                billings
        );

        System.out.println(
                "Billings creados: "
                        + billings.size()
        );
    }

    private BigDecimal getUnitPrice(
            PropertyType type
    ) {

        return switch (type) {

            case HOUSE ->
                    BigDecimal.valueOf(1.20);

            case BUSINESS ->
                    BigDecimal.valueOf(1.80);

            case INDUSTRIAL ->
                    BigDecimal.valueOf(2.50);
        };
    }

    private BillingStatus generateStatus() {

        double value =
                random.nextDouble();

        if (value < 0.70) {

            return BillingStatus.PAID;
        }

        if (value < 0.85) {

            return BillingStatus.PENDING;
        }

        if (value < 0.95) {

            return BillingStatus.OVERDUE;
        }

        return BillingStatus.PARTIALLY_PAID;
    }
}