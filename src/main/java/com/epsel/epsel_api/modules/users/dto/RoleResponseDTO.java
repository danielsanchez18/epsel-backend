package com.epsel.epsel_api.modules.users.dto;

import com.epsel.epsel_api.modules.users.enums.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class RoleResponseDTO {

    private UUID id;
    private RoleType name;
    private String description;

}