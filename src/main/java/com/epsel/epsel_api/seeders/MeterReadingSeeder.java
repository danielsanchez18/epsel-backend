package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MeterReadingSeeder {

    private final MeterReadingRepository repository;
    private final SupplyRepository supplyRepository;

    private final Random random = new Random();

    public void generate(int months) {

        if (repository.count() > 0) {
            return;
        }

        List<Supply> supplies =
                supplyRepository.findAll();

        List<MeterReading> readings =
                new ArrayList<>();

        for (Supply supply : supplies) {

            Integer currentReading =
                    supply.getLastReading();

            LocalDate startDate =
                    LocalDate.now()
                            .minusMonths(months - 1);

            for (int i = 0; i < months; i++) {

                Integer previousReading =
                        currentReading;

                Integer consumption =
                        generateConsumption(
                                supply.getSupplyType()
                        );

                currentReading =
                        previousReading + consumption;

                MeterReading reading =
                        new MeterReading();

                reading.setSupply(supply);

                reading.setPreviousReading(
                        previousReading
                );

                reading.setCurrentReading(
                        currentReading
                );

                reading.setConsumption(
                        consumption
                );

                reading.setReadingDate(
                        startDate.plusMonths(i)
                );

                reading.setStatus(
                        ReadingStatus.BILLED
                );

                reading.setMeterPhotoUrl(null);

                reading.setOcrValue(
                        String.valueOf(
                                currentReading
                        )
                );

                reading.setObservations(
                        generateObservation(
                                consumption
                        )
                );

                readings.add(reading);
            }

            supply.setLastReading(
                    currentReading
            );
        }

        repository.saveAll(readings);

        System.out.println(
                "MeterReadings creadas: "
                        + readings.size()
        );
    }

    private Integer generateConsumption(
            PropertyType type
    ) {

        return switch (type) {

            case HOUSE ->
                    8 + random.nextInt(25);

            case BUSINESS ->
                    20 + random.nextInt(80);

            case INDUSTRIAL ->
                    100 + random.nextInt(500);
        };
    }

    private ReadingStatus generateStatus(
            int currentMonth,
            int totalMonths
    ) {

        return ReadingStatus.BILLED;
    }

    private String generateObservation(
            Integer consumption
    ) {

        if (consumption > 300) {

            return "Consumo elevado";
        }

        if (consumption < 5) {

            return "Consumo inusualmente bajo";
        }

        return null;
    }
}
