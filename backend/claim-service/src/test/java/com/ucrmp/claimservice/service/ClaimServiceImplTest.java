package com.ucrmp.claimservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private UUID testUserId;
    private Claim testClaim;
    private CreateClaimRequest createRequest;
    private JsonNode testMetadataNode;

    @BeforeEach
    void setUp() throws Exception {
        testUserId = UUID.randomUUID();

        // Use a real ObjectMapper (not the mock) just for setup
        ObjectMapper realObjectMapper = new ObjectMapper();
        testMetadataNode = realObjectMapper.readTree("{\"hotelName\":\"Test Hotel\",\"flightNumber\":\"TEST123\"}");

        createRequest = new CreateClaimRequest();
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setClaimType(ClaimType.TRAVEL);
        createRequest.setDescription("Test travel claim");
        createRequest.setMetadata(testMetadataNode);

        testClaim = new Claim();
        testClaim.setId(UUID.randomUUID());
        testClaim.setUserId(testUserId);
        testClaim.setAmount(createRequest.getAmount());
        testClaim.setClaimType(createRequest.getClaimType());
        testClaim.setDescription(createRequest.getDescription());
        testClaim.setStatus(ClaimStatus.SUBMITTED);
        testClaim.setMetadata(testMetadataNode.toString());
    }

    @Test
    void createClaim_Success() throws Exception {
        // --- Arrange ---
        String metadataString = testMetadataNode.toString();
        
        // Mock the validation logic in 'validateAndConvertMetadata'
        when(objectMapper.treeToValue(any(JsonNode.class), any(Class.class)))
            .thenReturn(new com.ucrmp.claimservice.dto.ClaimMetadata.TravelMetadata("Test Hotel", "TEST123"));
        when(objectMapper.writeValueAsString(any())).thenReturn(metadataString);
        
        // Mock the repository save
        when(claimRepository.save(any(Claim.class))).thenReturn(testClaim);

        // --- THIS IS THE FIX ---
        // Mock the call inside 'mapToClaimResponse'
        // We tell the mock mapper what to do when it sees our metadata string
        when(objectMapper.readTree(testClaim.getMetadata())).thenReturn(testMetadataNode);
        // -------------------------

        // --- Act ---
        ClaimResponse response = claimService.createClaim(createRequest, testUserId);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(testClaim.getId(), response.getId());
        assertEquals(testUserId, response.getUserId());
        // Assert that the JsonNode is correct
        assertEquals(testMetadataNode, response.getMetadata());

        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void getClaimsByUserId_Success() throws Exception {
        // --- Arrange ---
        when(claimRepository.findByUserId(testUserId)).thenReturn(List.of(testClaim));

        // This mock is also required here
        when(objectMapper.readTree(testClaim.getMetadata())).thenReturn(testMetadataNode);

        // --- Act ---
        List<ClaimResponse> responses = claimService.getClaimsByUserId(testUserId);

        // --- Assert ---
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testClaim.getId(), responses.get(0).getId());
        assertEquals(testMetadataNode, responses.get(0).getMetadata());

        verify(claimRepository, times(1)).findByUserId(testUserId);
    }
}