package com.ucrmp.claimservice.controller;

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.service.ClaimService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// IMPORT: This is the new import
import org.springframework.web.bind.annotation.RequestHeader; 
// DELETE: import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private static final Logger log = LoggerFactory.getLogger(ClaimController.class);
    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * Creates a new claim for the user.
     * The user's ID is injected from the "X-User-Id" header,
     * which is securely added by the API Gateway.
     */
    @PostMapping
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request,
            // UPDATED: Read the User ID from the header
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Received request to create claim for user ID: {}", userId);
        ClaimResponse response = claimService.createClaim(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets all claims for the user.
     * The user's ID is injected from the "X-User-Id" header.
     */
    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getClaimsForUser(
            // UPDATED: Read the User ID from the header
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Received request to get all claims for user ID: {}", userId);
        List<ClaimResponse> claims = claimService.getClaimsByUserId(userId);
        return ResponseEntity.ok(claims);
    }
}