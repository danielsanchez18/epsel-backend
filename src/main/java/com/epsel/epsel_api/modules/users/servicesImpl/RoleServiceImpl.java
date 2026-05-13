package com.epsel.epsel_api.modules.users.servicesImpl;

import com.epsel.epsel_api.modules.users.dto.RoleResponseDTO;
import com.epsel.epsel_api.modules.users.mappers.RoleMapper;
import com.epsel.epsel_api.modules.users.repositories.RoleRepository;
import com.epsel.epsel_api.modules.users.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleResponseDTO> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
