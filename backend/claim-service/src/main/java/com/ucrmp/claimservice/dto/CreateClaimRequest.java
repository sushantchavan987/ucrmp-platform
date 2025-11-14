package com.ucrmp.claimservice.dto;

// This is the new import you need
import com.fasterxml.jackson.databind.JsonNode;
import com.ucrmp.claimservice.model.ClaimType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateClaimRequest {

    @NotNull(message = "Claim type is required")
    private ClaimType claimType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Size(max = 1000, message = "Description can be up to 1000 characters")
    private String description;

    // --- THIS IS THE NEW FIELD ---
    @NotNull(message = "Metadata is required for the claim type")
    private JsonNode metadata;
    // ----------------------------

    // --- Getters and Setters ---
    public ClaimType getClaimType() { return claimType; }
    public void setClaimType(ClaimType claimType) { this.claimType = claimType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // --- THIS IS THE NEW GETTER/SETTER ---
    public JsonNode getMetadata() { return metadata; }
    public void setMetadata(JsonNode metadata) { this.metadata = metadata; }
    // -------------------------------------
}