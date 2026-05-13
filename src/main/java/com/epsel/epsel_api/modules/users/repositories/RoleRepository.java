package com.epsel.epsel_api.modules.users.repositories;

import com.epsel.epsel_api.modules.users.entities.Role;
import com.epsel.epsel_api.modules.users.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleType name);

}
