package com.epsel.epsel_api.modules.customers.dto;

import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomerResponseDTO {

    private UUID id;
    private CustomerType type;
    private String documentNumber;
    private String fullName;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}