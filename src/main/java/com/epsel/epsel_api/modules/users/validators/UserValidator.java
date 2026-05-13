package com.epsel.epsel_api.modules.users.validators;

import com.epsel.epsel_api.modules.users.dto.CreateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UpdateUserDTO;
import com.epsel.epsel_api.modules.users.repositories.RoleRepository;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /* Validar los datos de creación de usuario adicionalmente a las validaciones básicas de campos obligatorios y formato:
    * 1. Validar que el DNI y el email sean únicos en la base de datos
    * 2. Validar que el roleId exista en la base de datos */
    public void validateCreate(CreateUserDTO dto) {

        // 1. Validar que el DNI y el email sean únicos en la base de datos
        if (userRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con el DNI: " + dto.getDni());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Ya existe un usuario con el teléfono: " + dto.getPhone());
        }

        // 2. Validar que el roleId exista en la base de datos
        if (!roleRepository.existsById(dto.getRoleId())) {
            throw new IllegalArgumentException("El rol seleccionado no existe: " + dto.getRoleId());
        }
    }

    /* Validar los datos de actualización de usuario adicionalmente a las validaciones básicas de campos obligatorios y formato:
    * 1. Validar que el email sea único en la base de datos, excepto para el usuario que se está actualizando
    * 2. Validar que el roleId exista en la base de datos */
    public void validateUpdate(UpdateUserDTO dto, UUID userId) {

        // 1. Validar que el email sea único en la base de datos, excepto para el usuario que se está actualizando
        var existingByEmail = userRepository.findByEmail(dto.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(userId)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        var existingByPhone = userRepository.findByPhone(dto.getPhone());
        if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(userId)) {
            throw new IllegalArgumentException("Ya existe un usuario con el teléfono: " + dto.getPhone());
        }


        // 2. Validar que el roleId exista en la base de datos
        if (!roleRepository.existsById(dto.getRoleId())) {
            throw new IllegalArgumentException("El rol seleccionado no existe: " + dto.getRoleId());
        }

    }

}
