package com.ucrmp.claimservice.service;

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;

import java.util.List;
import java.util.UUID;

public interface ClaimService {

    ClaimResponse createClaim(CreateClaimRequest request, UUID userId);

    List<ClaimResponse> getClaimsByUserId(UUID userId);
}