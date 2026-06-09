package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.repository.SupplyOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class SupplyOperationSeeder {

    private final SupplyRepository supplyRepository;
    private final SupplyOperationRepository repository;

    private final Random random = new Random();

    public void generate() {

        if (repository.count() > 0) {
            return;
        }

        List<Supply> supplies =
                supplyRepository.findAll();

        for (Supply supply : supplies) {

            SupplyOperation installation =
                    new SupplyOperation();

            installation.setSupply(supply);

            installation.setOperationType(
                    SupplyOperationType.INSTALLATION
            );

            installation.setOperationDate(
                    supply.getInstallationDate()
            );

            installation.setPerformedBy(
                    "Sistema"
            );

            installation.setReason(
                    "Instalación inicial"
            );

            installation.setObservations(
                    "Instalación completada"
            );

            repository.save(installation);

            if (
                    supply.getStatus()
                            == SupplyStatus.CUT_OFF
            ) {

                SupplyOperation cut =
                        new SupplyOperation();

                cut.setSupply(supply);

                cut.setOperationType(
                        SupplyOperationType.CUT_OFF
                );

                cut.setOperationDate(
                        LocalDate.now()
                                .minusDays(
                                        random.nextInt(120)
                                )
                );

                cut.setPerformedBy(
                        "Operador"
                );

                cut.setReason(
                        "Deuda acumulada"
                );

                repository.save(cut);
            }

            if (
                    supply.getStatus()
                            == SupplyStatus.ACTIVE
                            &&
                            random.nextBoolean()
            ) {

                SupplyOperation reconnection =
                        new SupplyOperation();

                reconnection.setSupply(supply);

                reconnection.setOperationType(
                        SupplyOperationType.RECONNECTION
                );

                reconnection.setOperationDate(
                        LocalDate.now()
                                .minusDays(
                                        random.nextInt(180)
                                )
                );

                reconnection.setPerformedBy(
                        "Operador"
                );

                reconnection.setReason(
                        "Pago de deuda"
                );

                repository.save(
                        reconnection
                );
            }

            if (
                    random.nextInt(100) < 15
            ) {

                SupplyOperation meterChange =
                        new SupplyOperation();

                meterChange.setSupply(
                        supply
                );

                meterChange.setOperationType(
                        SupplyOperationType.METER_CHANGE
                );

                meterChange.setOperationDate(
                        LocalDate.now()
                                .minusDays(
                                        random.nextInt(365)
                                )
                );

                meterChange.setPerformedBy(
                        "Técnico"
                );

                meterChange.setReason(
                        "Medidor defectuoso"
                );

                repository.save(
                        meterChange
                );
            }
        }
    }
}
