package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerSeeder {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();

    public void generate(int amount) {

        if (customerRepository.count() > 0) {
            return;
        }

        User admin =
                userRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow();

        List<Customer> customers =
                new ArrayList<>();

        for (int i = 1; i <= amount; i++) {

            Customer customer =
                    new Customer();

            customer.setType(
                    random.nextDouble() < 0.90
                            ? CustomerType.PERSON
                            : CustomerType.COMPANY
            );

            if (customer.getType() == CustomerType.PERSON) {

                customer.setDocumentNumber(
                        generateDni()
                );

                customer.setFullName(
                        generatePersonName()
                );

            } else {

                customer.setDocumentNumber(
                        generateRuc()
                );

                customer.setFullName(
                        generateCompanyName()
                );
            }

            customer.setPhone(
                    "9" + randomNumber(8)
            );

            customer.setEmail(
                    "cliente" + i +
                            "@epsel.com"
            );

            customer.setCreatedBy(admin);
            customer.setUpdatedBy(admin);

            customers.add(customer);
        }

        customerRepository.saveAll(customers);
    }

    private String generateDni() {

        return randomNumber(8);
    }

    private String generateRuc() {

        return "20" + randomNumber(9);
    }

    private String randomNumber(int length) {

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0; i < length; i++) {

            sb.append(
                    random.nextInt(10)
            );
        }

        return sb.toString();
    }

    private String generatePersonName() {

        String[] names = {
                "Juan",
                "Carlos",
                "Luis",
                "Pedro",
                "Ana",
                "María",
                "Lucía",
                "Rosa"
        };

        String[] lastNames = {
                "Pérez",
                "Ramírez",
                "Torres",
                "Díaz",
                "Castillo",
                "Vásquez"
        };

        return names[random.nextInt(names.length)]
                + " "
                + lastNames[random.nextInt(lastNames.length)]
                + " "
                + lastNames[random.nextInt(lastNames.length)];
    }

    private String generateCompanyName() {

        String[] companies = {
                "Ferretería Norte",
                "Comercial Lambayeque",
                "Constructora Perú",
                "Industrias del Norte",
                "Agroindustrial Chiclayo"
        };

        return companies[
                random.nextInt(companies.length)
                ];
    }
}