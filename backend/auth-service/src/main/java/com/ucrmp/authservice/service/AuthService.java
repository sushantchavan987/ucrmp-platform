package com.ucrmp.authservice.service;

import com.ucrmp.authservice.dto.LoginRequest;
import com.ucrmp.authservice.dto.LoginResponse;
import com.ucrmp.authservice.dto.RegisterRequest;

public interface AuthService {
    void registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest); 
}