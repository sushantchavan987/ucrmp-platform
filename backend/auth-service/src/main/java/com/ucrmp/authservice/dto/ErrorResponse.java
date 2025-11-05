package com.ucrmp.authservice.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String description;

    // Constructor
    public ErrorResponse(int statusCode, LocalDateTime timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    // Getters
    public int getStatusCode() { return statusCode; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getDescription() { return description; }
}