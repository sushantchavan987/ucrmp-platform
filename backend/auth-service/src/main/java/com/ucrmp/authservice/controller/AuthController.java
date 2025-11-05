package com.ucrmp.authservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ucrmp.authservice.dto.LoginRequest;
import com.ucrmp.authservice.dto.LoginResponse;
import com.ucrmp.authservice.dto.RegisterRequest;
import com.ucrmp.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for new user registration.
     * NO try-catch block here.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        log.info("Received POST request for /register with email: {}", registerRequest.getEmail());
        authService.registerUser(registerRequest);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    /**
     * Endpoint for user login.
     * NO try-catch block here.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Received POST request for /login with email: {}", loginRequest.getEmail());
        LoginResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * A secured test endpoint to verify JWT authentication.
     */
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello(Authentication authentication) {
        String username = authentication.getName();
        log.info("Accessed /hello endpoint by authenticated user: {}", username);
        return ResponseEntity.ok("Hello, " + username + "! Your roles are: " + authentication.getAuthorities());
    }
}