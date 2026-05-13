package com.epsel.epsel_api.modules.users.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateUserDTO {

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

    private String photoUrl;

    @NotNull(message = "El rol es requerido")
    private UUID roleId;

}
