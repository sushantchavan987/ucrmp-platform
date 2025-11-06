package com.ucrmp.claimservice.service;

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.entity.Claim;
import com.ucrmp.claimservice.model.ClaimStatus;
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

    public ClaimServiceImpl(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public ClaimResponse createClaim(CreateClaimRequest request, UUID userId) {
        log.info("Creating new claim for user ID: {}", userId);

        Claim newClaim = new Claim();
        newClaim.setUserId(userId);
        newClaim.setClaimType(request.getClaimType());
        newClaim.setAmount(request.getAmount());
        newClaim.setDescription(request.getDescription());
        newClaim.setStatus(ClaimStatus.SUBMITTED); // Default status

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
        return response;
    }
}