package com.ucrmp.claimservice.controller;

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.service.ClaimService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * Creates a new claim for the authenticated user.
     * The user's ID is automatically injected from the JWT principal.
     */
    @PostMapping
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request,
            @AuthenticationPrincipal UUID userId) {
        
        log.info("Received request to create claim for user ID: {}", userId);
        ClaimResponse response = claimService.createClaim(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets all claims for the authenticated user.
     * The user's ID is automatically injected from the JWT principal.
     */
    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getClaimsForUser(
            @AuthenticationPrincipal UUID userId) {
        
        log.info("Received request to get all claims for user ID: {}", userId);
        List<ClaimResponse> claims = claimService.getClaimsByUserId(userId);
        return ResponseEntity.ok(claims);
    }
}