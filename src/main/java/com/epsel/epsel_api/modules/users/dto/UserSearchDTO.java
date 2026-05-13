package com.epsel.epsel_api.modules.users.dto;

import com.epsel.epsel_api.modules.users.enums.RoleType;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserSearchDTO {

    private String search;
    private UserStatus status;
    private RoleType role;
    private LocalDate startDate;
    private LocalDate endDate;

}