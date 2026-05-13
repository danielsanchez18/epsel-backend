package com.epsel.epsel_api.modules.customers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerDTO {

    private String fullName;
    private String phone;
    private String email;

}