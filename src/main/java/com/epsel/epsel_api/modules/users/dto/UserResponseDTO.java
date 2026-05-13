package com.epsel.epsel_api.modules.users.dto;

import com.epsel.epsel_api.modules.users.enums.RoleType;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponseDTO {

    private UUID id;
    private String dni;
    private String names;
    private String lastNames;
    private String phone;
    private String email;
    private UserStatus status;
    private String photoUrl;
    private RoleType role;
    private String createdAt;
    private String updatedAt;

}