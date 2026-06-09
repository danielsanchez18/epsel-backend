package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.incidents.entity.Incident;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import com.epsel.epsel_api.modules.incidents.repository.IncidentRepository;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class IncidentSeeder {

    private final IncidentRepository incidentRepository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final SupplyRepository supplyRepository;

    private final Random random = new Random();

    public void generate(int quantity) {

        if (incidentRepository.count() > 0) {
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        List<Property> properties = propertyRepository.findAll();
        List<Supply> supplies = supplyRepository.findAll();

        if (customers.isEmpty() ||
                properties.isEmpty() ||
                supplies.isEmpty()) {

            return;
        }

        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 8);

        long days =
                ChronoUnit.DAYS.between(
                        startDate,
                        endDate
                );

        for (int i = 1; i <= quantity; i++) {

            Supply supply =
                    supplies.get(
                            random.nextInt(
                                    supplies.size()
                            )
                    );

            Customer customer =
                    supply.getCustomer();

            Property property =
                    supply.getProperty();

            Incident incident = new Incident();

            incident.setIncidentNumber(
                    String.format(
                            "INC-%08d",
                            i
                    )
            );

            incident.setCustomer(customer);
            incident.setProperty(property);
            incident.setSupply(supply);

            IncidentType type =
                    randomIncidentType();

            incident.setType(type);

            incident.setPriority(
                    randomPriority()
            );

            IncidentStatus status =
                    randomStatus();

            incident.setStatus(status);

            incident.setTitle(
                    generateTitle(type)
            );

            incident.setDescription(
                    generateDescription(type)
            );

            LocalDate reportedDate =
                    startDate.plusDays(
                            random.nextInt(
                                    (int) days
                            )
                    );

            incident.setReportedDate(
                    reportedDate
            );

            if (status == IncidentStatus.RESOLVED
                    || status == IncidentStatus.CLOSED) {

                incident.setResolvedDate(
                        reportedDate.plusDays(
                                random.nextInt(20) + 1
                        )
                );

                incident.setResolution(
                        generateResolution(type)
                );
            }

            incidentRepository.save(
                    incident
            );
        }

        System.out.println(
                "Incidencias generadas: "
                        + quantity
        );
    }

    private IncidentType randomIncidentType() {

        IncidentType[] values =
                IncidentType.values();

        return values[
                random.nextInt(
                        values.length
                )
                ];
    }

    private IncidentPriority randomPriority() {

        int value =
                random.nextInt(100);

        if (value < 50)
            return IncidentPriority.LOW;

        if (value < 80)
            return IncidentPriority.MEDIUM;

        if (value < 95)
            return IncidentPriority.HIGH;

        return IncidentPriority.CRITICAL;
    }

    private IncidentStatus randomStatus() {

        int value =
                random.nextInt(100);

        if (value < 20)
            return IncidentStatus.OPEN;

        if (value < 35)
            return IncidentStatus.IN_PROGRESS;

        if (value < 75)
            return IncidentStatus.RESOLVED;

        if (value < 95)
            return IncidentStatus.CLOSED;

        return IncidentStatus.REJECTED;
    }

    private String generateTitle(
            IncidentType type
    ) {

        return switch (type) {

            case WATER_LEAK ->
                    "Fuga de agua reportada";

            case LOW_PRESSURE ->
                    "Baja presión de agua";

            case METER_DAMAGE ->
                    "Medidor dañado";

            case METER_REPLACEMENT ->
                    "Solicitud de reemplazo de medidor";

            case BILLING_COMPLAINT ->
                    "Reclamo por facturación";

            case PAYMENT_COMPLAINT ->
                    "Reclamo por pago";

            case SERVICE_INTERRUPTION ->
                    "Interrupción del servicio";

            case ABNORMAL_CONSUMPTION ->
                    "Consumo anormal detectado";

            case OCR_ANOMALY ->
                    "Lectura OCR inconsistente";

            case READING_ANOMALY ->
                    "Lectura fuera de rango";

            case SUPPLY_CUT_COMPLAINT ->
                    "Reclamo por corte de servicio";

            default ->
                    "Incidencia general";
        };
    }

    private String generateDescription(
            IncidentType type
    ) {

        return "Incidencia generada automáticamente para pruebas del sistema. Tipo: "
                + type.name();
    }

    private String generateResolution(
            IncidentType type
    ) {

        return switch (type) {

            case WATER_LEAK ->
                    "Se reparó la tubería afectada.";

            case LOW_PRESSURE ->
                    "Se normalizó la presión en la zona.";

            case BILLING_COMPLAINT ->
                    "Se verificó la facturación.";

            case PAYMENT_COMPLAINT ->
                    "Se validó el pago realizado.";

            case METER_DAMAGE ->
                    "Se reemplazó el medidor.";

            default ->
                    "Caso atendido y resuelto.";
        };
    }
}
