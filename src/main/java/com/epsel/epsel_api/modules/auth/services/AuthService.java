package com.epsel.epsel_api.modules.auth.services;

import com.epsel.epsel_api.modules.auth.dtos.AuthResponseDTO;
import com.epsel.epsel_api.modules.auth.dtos.LoginRequestDTO;
import com.epsel.epsel_api.modules.auth.dtos.ResetPasswordDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO dto);

    void requestPasswordChange(String email);

    void resetPassword(ResetPasswordDTO dto);

}