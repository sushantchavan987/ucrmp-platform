package com.ucrmp.claimservice.dto;

import com.ucrmp.claimservice.model.ClaimStatus;
import com.ucrmp.claimservice.model.ClaimType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClaimResponse {

    private UUID id;
    private UUID userId;
    private ClaimType claimType;
    private BigDecimal amount;
    private ClaimStatus status;
    private String description;
    private LocalDateTime createdAt;

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ClaimType getClaimType() { return claimType; }
    public void setClaimType(ClaimType claimType) { this.claimType = claimType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}