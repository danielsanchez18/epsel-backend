package com.epsel.epsel_api.modules.users.mappers;

import com.epsel.epsel_api.modules.users.dto.CreateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UpdateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UserResponseDTO;
import com.epsel.epsel_api.modules.users.entities.Role;
import com.epsel.epsel_api.modules.users.entities.User;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.name", target = "role")
    @Mapping(source = "photoUrl", target = "photoUrl")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserResponseDTO toResponse(User user);

    @Mapping(source = "roleId", target = "role")
    User toEntityFromCreate(CreateUserDTO dto);

    @Mapping(source = "roleId", target = "role")
    @Mapping(target = "photoUrl", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toEntityFromUpdate(UpdateUserDTO dto, @MappingTarget User user);

    // Método para convertir UUID a la entidad Role
    default Role map(UUID roleId) {
        if (roleId == null) {
            return null;
        }
        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}