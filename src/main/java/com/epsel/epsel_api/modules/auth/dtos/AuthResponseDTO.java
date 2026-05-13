package com.epsel.epsel_api.modules.auth.dtos;

import com.epsel.epsel_api.modules.users.enums.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthResponseDTO {

    private String token;
    private UUID userId;
    private String fullName;
    private String email;
    private RoleType role;

}