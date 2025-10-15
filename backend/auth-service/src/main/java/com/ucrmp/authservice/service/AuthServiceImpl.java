package com.ucrmp.authservice.service;

import org.springframework.stereotype.Service;

import com.ucrmp.authservice.dto.RegisterRequest;


@Service
// You MUST add "implements AuthService" here.
public class AuthServiceImpl implements AuthService {

    @Override // This annotation now works because the class is implementing the interface.
    public void registerUser(RegisterRequest registerRequest) {
        // TODO: Logic will go here.
    }
}