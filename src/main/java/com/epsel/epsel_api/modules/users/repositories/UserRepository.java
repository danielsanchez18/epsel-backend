package com.epsel.epsel_api.modules.users.repositories;

import com.epsel.epsel_api.modules.users.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends
        JpaRepository<User, UUID>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Boolean existsByDni(String dni);

    Boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByPhone(String phone);

}
