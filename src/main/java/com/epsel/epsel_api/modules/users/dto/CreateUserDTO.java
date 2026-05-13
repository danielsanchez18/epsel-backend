package com.epsel.epsel_api.modules.users.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserDTO {

    @NotBlank(message = "El DNI es requerido")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;

    @NotBlank(message = "Los nombres son requeridos")
    private String names;

    @NotBlank(message = "Los apellidos son requeridos")
    private String lastNames;

    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener exactamente 9 dígitos")
    private String phone;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico no es válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial")
    private String password;

    private String photoUrl;

    @NotNull(message = "El rol es requerido")
    private UUID roleId;

}