package com.epsel.epsel_api.modules.users.repositories;

import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
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

    long countByStatusAndDeletedFalse(UserStatus status);

    long countByStatusAndDeletedFalseAndCreatedAtAfter(UserStatus status, LocalDateTime dateTime);

}
