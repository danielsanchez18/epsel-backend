package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class InstallationRequestSeeder {

    private final InstallationRequestRepository repository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();

    public void generate() {

        if (repository.count() > 0) {
            return;
        }

        User admin =
                userRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow();

        List<Property> properties =
                propertyRepository.findAll();

        List<InstallationRequest> requests =
                new ArrayList<>();

        for (Property property : properties) {

            InstallationRequest request =
                    new InstallationRequest();

            request.setCustomer(
                    property.getCustomer()
            );

            request.setProperty(property);

            request.setInternalReference(
                    randomInternalReference()
            );

            request.setInstallationCost(
                    BigDecimal.valueOf(
                            80 + random.nextInt(121)
                    )
            );

            LocalDate requestedDate =
                    LocalDate.now()
                            .minusDays(
                                    random.nextInt(730)
                            );

            request.setRequestedDate(
                    requestedDate
            );

            double statusRandom =
                    random.nextDouble();

            if (statusRandom < 0.80) {

                request.setStatus(
                        InstallationRequestStatus.INSTALLED
                );

                request.setApprovedDate(
                        requestedDate.plusDays(
                                random.nextInt(10) + 1
                        )
                );

                request.setInstallationDate(
                        request.getApprovedDate()
                                .plusDays(
                                        random.nextInt(15) + 1
                                )
                );

                request.setApprovedBy(admin);
                request.setInstalledBy(admin);

            }

            else if (statusRandom < 0.90) {

                request.setStatus(
                        InstallationRequestStatus.APPROVED
                );

                request.setApprovedDate(
                        requestedDate.plusDays(
                                random.nextInt(10) + 1
                        )
                );

                request.setApprovedBy(admin);

            }

            else if (statusRandom < 0.95) {

                request.setStatus(
                        InstallationRequestStatus.REJECTED
                );

                request.setRejectedDate(
                        requestedDate.plusDays(
                                random.nextInt(10) + 1
                        )
                );

                request.setRejectedBy(admin);

                request.setObservations(
                        "Solicitud rechazada por documentación incompleta"
                );

            }

            else {

                request.setStatus(
                        InstallationRequestStatus.PENDING
                );

            }

            requests.add(request);
        }

        repository.saveAll(requests);

        System.out.println(
                "Installation Requests creadas: "
                        + requests.size()
        );
    }

    private String randomInternalReference() {

        String[] references = {
                "Piso 1",
                "Piso 2",
                "Departamento 101",
                "Departamento 202",
                "Tienda",
                "Local Comercial",
                "Casa Principal",
                "Almacén"
        };

        return references[
                random.nextInt(
                        references.length
                )
                ];
    }
}
