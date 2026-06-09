package com.epsel.epsel_api.seeders;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {

    private final CustomerSeeder customerSeeder;
    private final PropertySeeder propertySeeder;
    private final InstallationRequestSeeder installationRequestSeeder;
    private final SupplySeeder supplySeeder;
    private final MeterReadingSeeder meterReadingSeeder;
    private final BillingSeeder billingSeeder;
    private final PaymentSeeder paymentSeeder;
    private final IncidentSeeder incidentSeeder;
    private final SupplyWorkOrderSeeder supplyWorkOrderSeeder;
    private final SupplyOperationSeeder supplyOperationSeeder;

    @Override
    public void run(String... args) {

        customerSeeder.generate(1000);
        propertySeeder.generate();
        installationRequestSeeder.generate();
        supplySeeder.generate();
        meterReadingSeeder.generate(24);
        billingSeeder.generate();
        paymentSeeder.generate();
        incidentSeeder.generate(3000);
        supplyWorkOrderSeeder.generate();
        supplyOperationSeeder.generate();

    }
}