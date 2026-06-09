package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
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
public class SupplySeeder {

    private final SupplyRepository supplyRepository;
    private final InstallationRequestRepository installationRequestRepository;

    private final Random random = new Random();

    public void generate() {

        if (supplyRepository.count() > 0) {
            return;
        }

        List<InstallationRequest> installations =
                installationRequestRepository
                        .findAll()
                        .stream()
                        .filter(i ->
                                i.getStatus() ==
                                        InstallationRequestStatus.INSTALLED
                        )
                        .toList();

        List<Supply> supplies =
                new ArrayList<>();

        int supplySequence = 1;
        int meterSequence = 1;

        for (InstallationRequest installation : installations) {

            Supply supply =
                    new Supply();

            supply.setSupplyNumber(
                    String.format(
                            "SUM-%08d",
                            supplySequence++
                    )
            );

            supply.setMeterNumber(
                    String.format(
                            "MED-%08d",
                            meterSequence++
                    )
            );

            supply.setProperty(
                    installation.getProperty()
            );

            supply.setCustomer(
                    installation.getCustomer()
            );

            supply.setInstallationRequest(
                    installation
            );

            double value = random.nextDouble();

            if (value < 0.85) {

                supply.setStatus(
                        SupplyStatus.ACTIVE
                );

                supply.setConnected(true);

            }
            else if (value < 0.95) {

                supply.setStatus(
                        SupplyStatus.SUSPENDED
                );

                supply.setConnected(false);

            }
            else {

                supply.setStatus(
                        SupplyStatus.CUT_OFF
                );

                supply.setConnected(false);

                supply.setCutOffDate(
                        LocalDate.now()
                                .minusDays(
                                        random.nextInt(90)
                                )
                );

                supply.setCutOffReason(
                        "Deuda acumulada"
                );
            }

            supply.setConnected(true);

            supply.setSupplyType(
                    installation.getProperty()
                            .getType()
            );

            supply.setInternalReference(
                    installation.getInternalReference()
            );

            supply.setInstallationDate(
                    installation.getInstallationDate()
            );

            supply.setActivationDate(
                    installation.getInstallationDate()
                            .plusDays(
                                    random.nextInt(3) + 1
                            )
            );

            supply.setLastReading(
                    100 + random.nextInt(300)
            );

            supplies.add(supply);
        }

        supplyRepository.saveAll(supplies);

        System.out.println(
                "Supplies creados: "
                        + supplies.size()
        );
    }
}
