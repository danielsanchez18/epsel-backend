package com.epsel.epsel_api.modules.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotBlank(message = "La contraseña actual es requerida")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es requerida")
    private String newPassword;
}
