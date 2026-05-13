package com.epsel.epsel_api.modules.auth.utils;

import com.epsel.epsel_api.modules.auth.security.AuthUserDetails;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthUserDetails userDetails)) {
            throw new BadRequestException("Autenticación inválida");
        }

        return userDetails.getUser();
    }
}