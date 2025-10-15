package com.ucrmp.authservice.service;

import com.ucrmp.authservice.dto.RegisterRequest;

public interface AuthService {
    // This is the contract that the implementation must follow.
    void registerUser(RegisterRequest registerRequest);
}