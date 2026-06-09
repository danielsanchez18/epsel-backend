package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplyWorkOrder.entity.SupplyWorkOrder;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class SupplyWorkOrderSeeder {

    private final SupplyRepository supplyRepository;
    private final SupplyWorkOrderRepository repository;

    private final Random random = new Random();

    public void generate() {

        if (repository.count() > 0) {
            return;
        }

        List<Supply> supplies = supplyRepository.findAll();

        for (Supply supply : supplies) {

            int orders = random.nextInt(4) + 1;

            LocalDate currentDate =
                    supply.getInstallationDate();

            for (int i = 0; i < orders; i++) {

                SupplyWorkOrder order =
                        new SupplyWorkOrder();

                WorkOrderType type =
                        randomType();

                WorkOrderStatus status =
                        randomStatus();

                order.setSupply(supply);
                order.setType(type);
                order.setStatus(status);

                order.setRequestedDate(currentDate);

                order.setScheduledDate(
                        currentDate.plusDays(
                                random.nextInt(10) + 1
                        )
                );

                if (
                        status == WorkOrderStatus.COMPLETED
                                || status == WorkOrderStatus.FAILED
                ) {

                    order.setCompletedDate(
                            order.getScheduledDate()
                                    .plusDays(
                                            random.nextInt(5)
                                    )
                    );
                }

                order.setReason(
                        generateReason(type)
                );

                order.setObservations(
                        "Orden generada automáticamente"
                );

                repository.save(order);

                currentDate =
                        currentDate.plusMonths(
                                random.nextInt(6) + 1
                        );
            }
        }
    }

    private WorkOrderType randomType() {

        WorkOrderType[] values =
                WorkOrderType.values();

        return values[
                random.nextInt(values.length)
                ];
    }

    private WorkOrderStatus randomStatus() {

        WorkOrderStatus[] values =
                WorkOrderStatus.values();

        return values[
                random.nextInt(values.length)
                ];
    }

    private String generateReason(
            WorkOrderType type
    ) {

        return switch (type) {

            case INSTALLATION ->
                    "Nueva instalación";

            case SUSPENSION ->
                    "Suspensión por deuda";

            case CUT_OFF ->
                    "Corte programado";

            case RECONNECTION ->
                    "Pago registrado";

            case INSPECTION ->
                    "Inspección preventiva";

            case METER_CHANGE ->
                    "Cambio de medidor";

        };
    }
}
