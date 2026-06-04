package com.epsel.epsel_api.modules.users.services;

import com.epsel.epsel_api.modules.users.dto.CreateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UpdateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UserResponseDTO;
import com.epsel.epsel_api.modules.users.dto.UserSearchDTO;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {

    UserResponseDTO create(
            CreateUserDTO dto,
            MultipartFile photo
    );

    UserResponseDTO update(
            UUID id,
            UpdateUserDTO dto,
            MultipartFile photo
    );

    UserResponseDTO getById(UUID id);

    Page<UserResponseDTO> getAll(
            UserSearchDTO searchDTO,
            Pageable pageable
    );

    void delete(UUID id);

    void changeStatus(UUID id, UserStatus status);

    com.epsel.epsel_api.modules.users.dto.WorkerKpisDTO getWorkerKpis();
}
