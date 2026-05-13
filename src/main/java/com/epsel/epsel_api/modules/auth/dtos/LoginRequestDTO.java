package com.epsel.epsel_api.modules.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico no es válido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

}