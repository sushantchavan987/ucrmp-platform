package com.ucrmp.claimservice.service;

// --- NEW IMPORTS ---
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucrmp.claimservice.dto.ClaimMetadata;
// ---------------------

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.entity.Claim;
import com.ucrmp.claimservice.model.ClaimStatus;
import com.ucrmp.claimservice.model.ClaimType;
import com.ucrmp.claimservice.repository.ClaimRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimServiceImpl implements ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);

    private final ClaimRepository claimRepository;

    // --- NEW: Inject the ObjectMapper ---
    // This is the standard Java library for handling JSON.
    // Spring Boot provides this for us automatically.
    private final ObjectMapper objectMapper;

    // --- NEW: Updated Constructor ---
    public ClaimServiceImpl(ClaimRepository claimRepository, ObjectMapper objectMapper) {
        this.claimRepository = claimRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ClaimResponse createClaim(CreateClaimRequest request, UUID userId) {
        log.info("Creating new claim for user ID: {}", userId);

        // --- NEW: Validate the metadata ---
        String metadataJson = validateAndConvertMetadata(request.getClaimType(), request.getMetadata());
        // ----------------------------------

        Claim newClaim = new Claim();
        newClaim.setUserId(userId);
        newClaim.setClaimType(request.getClaimType());
        newClaim.setAmount(request.getAmount());
        newClaim.setDescription(request.getDescription());
        newClaim.setStatus(ClaimStatus.SUBMITTED); // Default status

        // --- NEW: Set the validated metadata string ---
        newClaim.setMetadata(metadataJson);
        // ------------------------------------------

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

    // --- NEW: Helper method to validate metadata ---
    private String validateAndConvertMetadata(ClaimType type, JsonNode metadataNode) {
        try {
            // Use a 'switch' to check the claim type
            switch (type) {
                case TRAVEL:
                    // Convert the generic JSON into our specific, validated contract.
                    // If 'hotelName' or 'flightNumber' is missing, this will fail.
                    ClaimMetadata.TravelMetadata travelData = 
                        objectMapper.treeToValue(metadataNode, ClaimMetadata.TravelMetadata.class);

                    // We can add more complex validation here if needed

                    // Convert it back to a string to be saved in the DB
                    return objectMapper.writeValueAsString(travelData);

                case MEDICAL:
                    // Do the same for the medical contract
                    ClaimMetadata.MedicalMetadata medicalData = 
                        objectMapper.treeToValue(metadataNode, ClaimMetadata.MedicalMetadata.class);

                    // This will have already checked the @Size(min=5) for prescriptionNumber

                    return objectMapper.writeValueAsString(medicalData);

                // Add cases for MEAL, OFFICE_SUPPLIES, OTHER as you define them
                default:
                    log.warn("No specific metadata validation for type: {}. Saving as-is.", type);
                    return metadataNode.toString();
            }
        } catch (JsonProcessingException e) {
            // This will be caught by our GlobalExceptionHandler
            log.error("Failed to parse and validate metadata: {}", e.getMessage());
            throw new RuntimeException("Invalid metadata format for claim type " + type);
        } catch (IllegalArgumentException e) {
             // This catches validation failures (like @NotBlank)
            log.error("Metadata validation failed: {}", e.getMessage());
            throw new RuntimeException("Metadata validation failed: " + e.getMessage());
        }
    }
    // -------------------------------------------------

    // A private helper method to convert our Entity to a DTO
 // A private helper method to convert our Entity to a DTO
    private ClaimResponse mapToClaimResponse(Claim claim) {
        ClaimResponse response = new ClaimResponse();
        response.setId(claim.getId());
        response.setUserId(claim.getUserId());
        response.setClaimType(claim.getClaimType());
        response.setAmount(claim.getAmount());
        response.setStatus(claim.getStatus());
        response.setDescription(claim.getDescription());
        response.setCreatedAt(claim.getCreatedAt());
        
        // --- THIS IS THE FIX ---
        // We use the ObjectMapper to convert the metadata String from the
        // database back into a real JsonNode object for the response.
        try {
            JsonNode metadataNode = objectMapper.readTree(claim.getMetadata());
            response.setMetadata(metadataNode);
        } catch (Exception e) {
            log.error("Failed to parse metadata from database for claim ID: {}", claim.getId(), e);
            response.setMetadata(null); // or set an error node
        }
        // -------------------------
        
        return response;
    }
}