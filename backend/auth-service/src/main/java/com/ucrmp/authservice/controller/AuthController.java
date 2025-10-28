package com.ucrmp.authservice.controller;

import com.ucrmp.authservice.dto.LoginRequest;
import com.ucrmp.authservice.dto.LoginResponse;
import com.ucrmp.authservice.dto.RegisterRequest;
import com.ucrmp.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    // Add this new method for logging in
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}