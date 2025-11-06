package com.ucrmp.claimservice.service;

import com.ucrmp.claimservice.dto.ClaimResponse;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.entity.Claim;
import com.ucrmp.claimservice.model.ClaimStatus;
import com.ucrmp.claimservice.model.ClaimType;
import com.ucrmp.claimservice.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito
class ClaimServiceImplTest {

    // 1. Create a "fake" repository
    @Mock
    private ClaimRepository claimRepository;

    // 2. Create a real service and inject the fake repository
    @InjectMocks
    private ClaimServiceImpl claimService;

    private UUID testUserId;
    private Claim testClaim;
    private CreateClaimRequest createRequest;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        createRequest = new CreateClaimRequest();
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setClaimType(ClaimType.TRAVEL);
        createRequest.setDescription("Test travel claim");

        testClaim = new Claim();
        testClaim.setId(UUID.randomUUID());
        testClaim.setUserId(testUserId);
        testClaim.setAmount(createRequest.getAmount());
        testClaim.setClaimType(createRequest.getClaimType());
        testClaim.setDescription(createRequest.getDescription());
        testClaim.setStatus(ClaimStatus.SUBMITTED);
    }

    @Test
    void createClaim_Success() {
        // --- Arrange ---
        // "When the save method is called, return our pre-built testClaim"
        when(claimRepository.save(any(Claim.class))).thenReturn(testClaim);

        // --- Act ---
        // Call the method we are testing
        ClaimResponse response = claimService.createClaim(createRequest, testUserId);

        // --- Assert ---
        // Check that the response is not null and has the correct data
        assertNotNull(response);
        assertEquals(testClaim.getId(), response.getId());
        assertEquals(testUserId, response.getUserId());
        assertEquals(ClaimStatus.SUBMITTED, response.getStatus());
        assertEquals("100.00", response.getAmount().toString());

        // Verify that the save method was called exactly once
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void getClaimsByUserId_Success() {
        // --- Arrange ---
        // "When findByUserId is called, return a list containing our testClaim"
        when(claimRepository.findByUserId(testUserId)).thenReturn(List.of(testClaim));

        // --- Act ---
        List<ClaimResponse> responses = claimService.getClaimsByUserId(testUserId);

        // --- Assert ---
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testClaim.getId(), responses.get(0).getId());

        // Verify that the findByUserId method was called
        verify(claimRepository, times(1)).findByUserId(testUserId);
    }
}