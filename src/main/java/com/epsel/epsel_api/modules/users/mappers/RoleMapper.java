package com.epsel.epsel_api.modules.users.mappers;

import com.epsel.epsel_api.modules.users.dto.RoleResponseDTO;
import com.epsel.epsel_api.modules.users.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponseDTO toResponse(Role role);

}
