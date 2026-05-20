package com.epsel.epsel_api.modules.configurations.config;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceZoneSeeder implements CommandLineRunner {

    private final ServiceZoneRepository serviceZoneRepository;

    @Override
    public void run(String... args) throws Exception {

        if (serviceZoneRepository.count() > 0) {
            return;
        }

        ServiceZone zone1 = new ServiceZone();
        zone1.setName("Rural Residencial");
        zone1.setDescription("Viviendas rurales");
        zone1.setActive(true);

        ServiceZone zone2 = new ServiceZone();
        zone2.setName("Gobierno");
        zone2.setDescription("Instituciones públicas");
        zone2.setActive(true);

        ServiceZone zone3 = new ServiceZone();
        zone3.setName("Comercial");
        zone3.setDescription("Negocios pequeños");
        zone3.setActive(true);

        ServiceZone zone4 = new ServiceZone();
        zone4.setName("Industrial");
        zone4.setDescription("Fábricas y grandes empresas");
        zone4.setActive(true);

        ServiceZone zone5 = new ServiceZone();
        zone5.setName("Apoyo Social");
        zone5.setDescription("Tarifas sociales/subsidiadas");
        zone5.setActive(true);

        serviceZoneRepository.save(zone1);
        serviceZoneRepository.save(zone2);
        serviceZoneRepository.save(zone3);
        serviceZoneRepository.save(zone4);
        serviceZoneRepository.save(zone5);

    }
}
