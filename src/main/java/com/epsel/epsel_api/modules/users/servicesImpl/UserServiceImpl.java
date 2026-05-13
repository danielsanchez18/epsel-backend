package com.epsel.epsel_api.modules.users.servicesImpl;

import com.epsel.epsel_api.modules.users.dto.CreateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UpdateUserDTO;
import com.epsel.epsel_api.modules.users.dto.UserResponseDTO;
import com.epsel.epsel_api.modules.users.dto.UserSearchDTO;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import com.epsel.epsel_api.modules.users.mappers.UserMapper;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import com.epsel.epsel_api.modules.users.services.UserService;
import com.epsel.epsel_api.modules.users.specifications.UserSpecification;
import com.epsel.epsel_api.modules.users.validators.UserValidator;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import com.epsel.epsel_api.shared.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    @Override
    public UserResponseDTO create(CreateUserDTO dto, MultipartFile image) {

        User user = mapper.toEntityFromCreate(dto);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.upload(image);
            user.setPhotoUrl(imageUrl);
        }

        validator.validateCreate(dto);

        User savedUser = repository.save(user);
        return mapper.toResponse(savedUser);
    }

    @Override
    public UserResponseDTO update(UUID id, UpdateUserDTO dto, MultipartFile photo) {

        User userExisting = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        mapper.toEntityFromUpdate(dto, userExisting);

         if (photo != null && !photo.isEmpty()) {
            String photoUrl = storageService.upload(photo);
            userExisting.setPhotoUrl(photoUrl);
        }

        validator.validateUpdate(dto, id);
        return mapper.toResponse(repository.save(userExisting));
    }

    @Override
    public UserResponseDTO getById(UUID id) {
        User user = repository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapper.toResponse(user);
    }

    @Override
    public Page<UserResponseDTO> getAll(UserSearchDTO searchDTO, Pageable pageable) {
        return repository.findAll(
                        UserSpecification.filter(searchDTO),
                        pageable)
                .map(mapper::toResponse);
    }

    @Override
    public void delete(UUID id) {
        User user = repository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setStatus(UserStatus.SUSPENDED);
        repository.save(user);
    }

    @Override
    public void changeStatus(UUID id, UserStatus status) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setStatus(status);
        repository.save(user);
    }
}