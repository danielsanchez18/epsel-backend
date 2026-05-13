package com.epsel.epsel_api.modules.users.services;

import com.epsel.epsel_api.modules.users.dto.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> getAll();
}
