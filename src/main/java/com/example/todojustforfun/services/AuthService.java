package com.example.todojustforfun.services;

import com.example.todojustforfun.dto.LoginRequest;
import com.example.todojustforfun.dto.RegisterRequest;
import com.example.todojustforfun.dto.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
    UserResponse getCurrentUser();
    void logout();
}
