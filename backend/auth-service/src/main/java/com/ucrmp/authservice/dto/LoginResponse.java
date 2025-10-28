package com.ucrmp.authservice.dto;

public class LoginResponse {
    private String token;

    // --- Constructor ---
    public LoginResponse(String token) {
        this.token = token;
    }

    // --- Getter ---
    public String getToken() {
        return token;
    }
}