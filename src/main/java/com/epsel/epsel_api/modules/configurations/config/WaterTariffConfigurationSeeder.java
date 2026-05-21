package com.epsel.epsel_api.modules.configurations.config;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.TariffStatus;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.configurations.repositories.WaterTariffConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WaterTariffConfigurationSeeder implements CommandLineRunner {

    private final WaterTariffConfigurationRepository repository;
    private final ServiceZoneRepository zoneRepository;

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return;
        }

        List<ServiceZone> zones = zoneRepository.findAll();

        for (ServiceZone zone : zones) {

            switch (zone.getName()) {

                case "Urbana Residencial" -> create(
                        zone,
                        "2.50",
                        "8.00",
                        "18.00"
                );

                case "Rural Residencial" -> create(
                        zone,
                        "1.80",
                        "5.00",
                        "18.00"
                );

                case "Comercial" -> create(
                        zone,
                        "3.20",
                        "15.00",
                        "18.00"
                );

                case "Industrial" -> create(
                        zone,
                        "4.50",
                        "30.00",
                        "18.00"
                );

                case "Gobierno" -> create(
                        zone,
                        "3.00",
                        "20.00",
                        "18.00"
                );

                case "Apoyo Social" -> create(
                        zone,
                        "1.20",
                        "2.00",
                        "0.00"
                );
            }
        }

        System.out.println(
                "WaterTariffConfiguration seeded successfully"
        );
    }

    private void create(
            ServiceZone zone,
            String pricePerM3,
            String fixedCharge,
            String taxPercentage
    ) {

        WaterTariffConfiguration configuration =
                new WaterTariffConfiguration();

        configuration.setZone(zone);

        configuration.setPricePerM3(
                new BigDecimal(pricePerM3)
        );

        configuration.setFixedCharge(
                new BigDecimal(fixedCharge)
        );

        configuration.setTaxPercentage(
                new BigDecimal(taxPercentage)
        );

        configuration.setEffectiveDate(
                LocalDate.now()
        );

        configuration.setActive(true);

        repository.save(configuration);
    }
}