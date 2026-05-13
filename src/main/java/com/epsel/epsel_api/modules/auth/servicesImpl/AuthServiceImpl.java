package com.epsel.epsel_api.modules.auth.servicesImpl;

import com.epsel.epsel_api.modules.auth.dtos.AuthResponseDTO;
import com.epsel.epsel_api.modules.auth.dtos.LoginRequestDTO;
import com.epsel.epsel_api.modules.auth.dtos.ResetPasswordDTO;
import com.epsel.epsel_api.modules.auth.security.JwtService;
import com.epsel.epsel_api.modules.auth.services.AuthService;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.enums.UserStatus;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Usuario inactivo");
        }

        boolean matches = passwordEncoder.matches(dto.getPassword(), user.getPassword());

        if (!matches) {
            throw new BadRequestException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getNames() + " " + user.getLastNames())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .build();
    }

    @Override
    public void requestPasswordChange(String email) {
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
    }
}