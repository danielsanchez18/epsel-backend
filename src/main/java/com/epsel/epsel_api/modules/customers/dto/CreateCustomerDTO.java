package com.epsel.epsel_api.modules.customers.dto;

import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomerDTO {

    @NotNull(message = "Tipo de cliente es requerido")
    private CustomerType type;

    @NotBlank(message = "Número de documento es requerido")
    private String documentNumber;

    @NotBlank(message = "Nombre completo es requerido")
    private String fullName;

    private String phone;
    private String email;

}