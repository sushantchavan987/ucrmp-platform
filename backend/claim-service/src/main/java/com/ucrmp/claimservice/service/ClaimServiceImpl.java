package com.ucrmp.claimservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucrmp.claimservice.dto.ClaimMetadata;
import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.entity.Claim;
import com.ucrmp.claimservice.model.ClaimStatus;
import com.ucrmp.claimservice.model.ClaimType;
import com.ucrmp.claimservice.repository.ClaimRepository;

// --- NEW IMPORTS ---
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
// -------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set; // NEW IMPORT
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimServiceImpl implements ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);

    private final ClaimRepository claimRepository;
    private final ObjectMapper objectMapper;
    
    // --- NEW: Inject the Validator ---
    private final Validator validator;

    // --- NEW: Updated Constructor ---
    public ClaimServiceImpl(ClaimRepository claimRepository, 
                            ObjectMapper objectMapper, 
                            Validator validator) { // Added validator
        this.claimRepository = claimRepository;
        this.objectMapper = objectMapper;
        this.validator = validator; // Added this
    }

    @Override
    public ClaimResponse createClaim(CreateClaimRequest request, UUID userId) {
        log.info("Creating new claim for user ID: {}", userId);

        String metadataJson = validateAndConvertMetadata(request.getClaimType(), request.getMetadata());

        Claim newClaim = new Claim();
        newClaim.setUserId(userId);
        newClaim.setClaimType(request.getClaimType());
        newClaim.setAmount(request.getAmount());
        newClaim.setDescription(request.getDescription());
        newClaim.setStatus(ClaimStatus.SUBMITTED);
        newClaim.setMetadata(metadataJson);

        Claim savedClaim = claimRepository.save(newClaim);

        log.info("Successfully created claim with ID: {}", savedClaim.getId());
        return mapToClaimResponse(savedClaim);
    }

    @Override
    public List<ClaimResponse> getClaimsByUserId(UUID userId) {
        log.info("Fetching all claims for user ID: {}", userId);
        List<Claim> claims = claimRepository.findByUserId(userId);
        return claims.stream()
                     .map(this::mapToClaimResponse)
                     .collect(Collectors.toList());
    }

    // --- UPDATED: Helper method to validate metadata ---
    private String validateAndConvertMetadata(ClaimType type, JsonNode metadataNode) {
        try {
            Object metadataObject; // Use a generic object to hold the result
            String metadataJson;

            switch (type) {
                case TRAVEL:
                    ClaimMetadata.TravelMetadata travelData = 
                        objectMapper.treeToValue(metadataNode, ClaimMetadata.TravelMetadata.class);
                    metadataObject = travelData; // Store it
                    metadataJson = objectMapper.writeValueAsString(travelData);
                    break;

                case MEDICAL:
                    ClaimMetadata.MedicalMetadata medicalData = 
                        objectMapper.treeToValue(metadataNode, ClaimMetadata.MedicalMetadata.class);
                    metadataObject = medicalData; // Store it
                    metadataJson = objectMapper.writeValueAsString(medicalData);
                    break;

                default:
                    log.warn("No specific metadata validation for type: {}. Saving as-is.", type);
                    return metadataNode.toString();
            }

            // --- THIS IS THE FIX ---
            // Manually trigger validation on the deserialized object
            validateMetadata(metadataObject);
            // -------------------------

            return metadataJson; // Return the validated JSON string

        } catch (JsonProcessingException e) {
            log.error("Failed to parse metadata: {}", e.getMessage());
            throw new RuntimeException("Invalid metadata format for claim type " + type);
        }
    }
    
    // --- NEW: Helper method to run the validator ---
    private <T> void validateMetadata(T metadataObject) {
        // Use the injected validator to check the object
        Set<ConstraintViolation<T>> violations = validator.validate(metadataObject);
        
        if (!violations.isEmpty()) {
            // If there are errors, collect them and throw an exception
            String errorMessages = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
                    
            log.warn("Metadata validation failed: {}", errorMessages);
            // This is a specific, new exception we will now catch
            throw new ConstraintViolationException("Validation failed: " + errorMessages, violations);
        }
    }
    // -------------------------------------------------

    // Updated to handle parsing the string back to a JsonNode
    private ClaimResponse mapToClaimResponse(Claim claim) {
        ClaimResponse response = new ClaimResponse();
        response.setId(claim.getId());
        response.setUserId(claim.getUserId());
        response.setClaimType(claim.getClaimType());
        response.setAmount(claim.getAmount());
        response.setStatus(claim.getStatus());
        response.setDescription(claim.getDescription());
        response.setCreatedAt(claim.getCreatedAt());
        
        try {
            // Convert the metadata String from the DB back into a real JsonNode
            JsonNode metadataNode = objectMapper.readTree(claim.getMetadata());
            response.setMetadata(metadataNode);
        } catch (Exception e) {
            log.error("Failed to parse metadata from database for claim ID: {}", claim.getId(), e);
            response.setMetadata(null);
        }
        
        return response;
    }
}