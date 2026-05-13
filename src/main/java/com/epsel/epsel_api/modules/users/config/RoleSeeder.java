package com.epsel.epsel_api.modules.users.config;

import com.epsel.epsel_api.modules.users.entities.Role;
import com.epsel.epsel_api.modules.users.enums.RoleType;
import com.epsel.epsel_api.modules.users.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotExists(RoleType.ADMIN, "Administrador del sistema");
        createRoleIfNotExists(RoleType.SUPERVISOR, "Supervisor de operaciones");
        createRoleIfNotExists(RoleType.TECHNICIAN, "Técnico de campo");
        createRoleIfNotExists(RoleType.VERIFIER, "Verificador de calidad");
        createRoleIfNotExists(RoleType.MANAGEMENT, "Gerente de área");
    }

    private void createRoleIfNotExists(RoleType roleType, String description) {
        boolean exists = roleRepository.findByName(roleType).isPresent();
        if (!exists) {
            Role role = new Role();
            role.setName(roleType);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }
}