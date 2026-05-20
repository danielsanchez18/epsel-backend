package com.epsel.epsel_api.modules.configurations.config;

import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceFeeConfigurationRepository;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceFeeConfigurationSeeder implements CommandLineRunner {

    private final ServiceFeeConfigurationRepository repository;
    private final ServiceZoneRepository zoneRepository;

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return;
        }

        List<ServiceZone> zones = zoneRepository.findAll();

        for (ServiceZone zone : zones) {

            switch (zone.getName()) {

                case "Urbana Residencial" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "60.00");
                    create(zone, ServiceFeeType.RECONNECTION, "40.00");
                    create(zone, ServiceFeeType.CUT, "25.00");
                    create(zone, ServiceFeeType.PENALTY, "15.00");
                }

                case "Rural Residencial" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "80.00");
                    create(zone, ServiceFeeType.RECONNECTION, "50.00");
                    create(zone, ServiceFeeType.CUT, "30.00");
                    create(zone, ServiceFeeType.PENALTY, "20.00");
                }

                case "Comercial" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "100.00");
                    create(zone, ServiceFeeType.RECONNECTION, "70.00");
                    create(zone, ServiceFeeType.CUT, "40.00");
                    create(zone, ServiceFeeType.PENALTY, "35.00");
                }

                case "Industrial" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "150.00");
                    create(zone, ServiceFeeType.RECONNECTION, "100.00");
                    create(zone, ServiceFeeType.CUT, "60.00");
                    create(zone, ServiceFeeType.PENALTY, "50.00");
                }

                case "Gobierno" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "120.00");
                    create(zone, ServiceFeeType.RECONNECTION, "80.00");
                    create(zone, ServiceFeeType.CUT, "50.00");
                    create(zone, ServiceFeeType.PENALTY, "40.00");
                }

                case "Apoyo Social" -> {

                    create(zone, ServiceFeeType.INSTALLATION, "30.00");
                    create(zone, ServiceFeeType.RECONNECTION, "15.00");
                    create(zone, ServiceFeeType.CUT, "10.00");
                    create(zone, ServiceFeeType.PENALTY, "5.00");
                }
            }
        }

        System.out.println(
                "ServiceFeeConfiguration seeded successfully"
        );
    }

    private void create(
            ServiceZone zone,
            ServiceFeeType feeType,
            String amount
    ) {

        ServiceFeeConfiguration configuration =
                new ServiceFeeConfiguration();

        configuration.setZone(zone);

        configuration.setFeeType(feeType);

        configuration.setAmount(
                new BigDecimal(amount)
        );

        configuration.setActive(true);

        repository.save(configuration);
    }
}
