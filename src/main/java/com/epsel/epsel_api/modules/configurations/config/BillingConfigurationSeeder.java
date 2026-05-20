package com.epsel.epsel_api.modules.configurations.config;

import com.epsel.epsel_api.modules.configurations.entities.BillingConfiguration;
import com.epsel.epsel_api.modules.configurations.repositories.BillingConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BillingConfigurationSeeder implements CommandLineRunner {

    private final BillingConfigurationRepository repository;

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return;
        }

        BillingConfiguration configuration = new BillingConfiguration();

        /* 2 meses sin pagar antes del corte */
        configuration.setMonthsBeforeCut(2);

        /* 5% de mora mensual */
        configuration.setLateInterestPercentage(new BigDecimal("5.00"));

        /* 15 días de plazo */
        configuration.setGraceDays(15);

        configuration.setActive(true);

        repository.save(configuration);
    }
}