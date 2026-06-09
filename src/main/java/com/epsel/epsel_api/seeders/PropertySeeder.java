package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertySeeder {

    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;
    private final ServiceZoneRepository serviceZoneRepository;

    private final Random random = new Random();

    public void generate() {

        if (propertyRepository.count() > 0) {
            return;
        }

        List<Customer> customers =
                customerRepository.findAll();

        List<ServiceZone> zones =
                serviceZoneRepository.findAll();

        List<Property> properties =
                new ArrayList<>();

        int cadastralSequence = 1;

        for (Customer customer : customers) {

            int propertyCount =
                    random.nextInt(3) + 1;

            for (int i = 0; i < propertyCount; i++) {

                Property property =
                        new Property();

                PropertyType type =
                        randomPropertyType();

                property.setCustomer(customer);

                property.setType(type);

                property.setZone(
                        randomZone(zones, type)
                );

                property.setCadastralCode(
                        String.format(
                                "CAT-%08d",
                                cadastralSequence++
                        )
                );

                property.setAddress(
                        generateAddress()
                );

                property.setReference(
                        "Referencia " +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                );

                property.setLatitude(
                        -6.7714 + random.nextDouble() / 100
                );

                property.setLongitude(
                        -79.8409 + random.nextDouble() / 100
                );

                properties.add(property);
            }
        }

        propertyRepository.saveAll(properties);
    }

    private PropertyType randomPropertyType() {

        double value =
                random.nextDouble();

        if (value < 0.80) {
            return PropertyType.HOUSE;
        }

        if (value < 0.95) {
            return PropertyType.BUSINESS;
        }

        return PropertyType.INDUSTRIAL;
    }

    private ServiceZone randomZone(
            List<ServiceZone> zones,
            PropertyType type
    ) {

        return switch (type) {

            case HOUSE ->
                    zones.stream()
                            .filter(z ->
                                    z.getName().equalsIgnoreCase(
                                            "Rural Residencial"
                                    ) ||
                                            z.getName().equalsIgnoreCase(
                                                    "Apoyo Social"
                                            )
                            )
                            .findAny()
                            .orElse(zones.get(0));

            case BUSINESS ->
                    zones.stream()
                            .filter(z ->
                                    z.getName().equalsIgnoreCase(
                                            "Comercial"
                                    )
                            )
                            .findAny()
                            .orElse(zones.get(0));

            case INDUSTRIAL ->
                    zones.stream()
                            .filter(z ->
                                    z.getName().equalsIgnoreCase(
                                            "Industrial"
                                    )
                            )
                            .findAny()
                            .orElse(zones.get(0));
        };
    }

    private String generateAddress() {

        return "Mz "
                + (random.nextInt(40) + 1)
                + " Lt "
                + (random.nextInt(20) + 1);
    }
}