package com.ucrmp.claimservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// This is a "sealed interface." It strictly defines
// which types of metadata are allowed in our system.
public sealed interface ClaimMetadata
    permits ClaimMetadata.TravelMetadata, ClaimMetadata.MedicalMetadata {

    // --- Contract 1: TRAVEL ---
    // A 'record' is a modern, simple data class.
    // We add validation directly to the fields.
    record TravelMetadata(
        @NotBlank(message = "Hotel name is required for travel claims") 
        String hotelName,
        
        @NotBlank(message = "Flight number is required for travel claims") 
        String flightNumber
    ) implements ClaimMetadata {}

    // --- Contract 2: MEDICAL ---
    record MedicalMetadata(
        @NotBlank(message = "Hospital name is required for medical claims") 
        String hospitalName,
        
        @NotBlank(message = "Prescription number is required") 
        @Size(min = 5, message = "Prescription number must be at least 5 characters") 
        String prescriptionNumber
    ) implements ClaimMetadata {}
}