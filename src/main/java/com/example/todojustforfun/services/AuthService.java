package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.LoginRequest;
import com.example.todojustforfun.dto.RegisterRequest;
import com.example.todojustforfun.dto.UserResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    UserResponse getCurrentUser(Authentication authentication);
}
