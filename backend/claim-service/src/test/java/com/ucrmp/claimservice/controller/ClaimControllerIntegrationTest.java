package com.ucrmp.claimservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucrmp.claimservice.dto.CreateClaimRequest;
import com.ucrmp.claimservice.model.ClaimType;
import com.ucrmp.claimservice.repository.ClaimRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClaimControllerIntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.28");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClaimRepository claimRepository;

    private final String MOCK_USER_ID = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11";

    @AfterEach
    void cleanup() {
        // Clean the database after each test
        claimRepository.deleteAll();
    }

    @Test
    void createClaim_Success() throws Exception {
        // --- Arrange ---
        CreateClaimRequest request = new CreateClaimRequest();
        request.setAmount(new BigDecimal("120.50"));
        request.setClaimType(ClaimType.MEDICAL);
        request.setDescription("Doctor visit");

        // --- Act ---
        mockMvc.perform(post("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        // THIS IS THE FIX: We now manually add the header
                        // to simulate the API Gateway.
                        .header("X-User-Id", MOCK_USER_ID)) 
        // --- Assert (The HTTP Response) ---
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(120.50))
                .andExpect(jsonPath("$.userId").value(MOCK_USER_ID));

        // --- Assert (The Database) ---
        assertEquals(1, claimRepository.findAll().size());
        assertEquals(UUID.fromString(MOCK_USER_ID), claimRepository.findAll().get(0).getUserId());
    }

    @Test
    void createClaim_ValidationFails_ReturnsBadRequest() throws Exception {
        // --- Arrange ---
        CreateClaimRequest badRequest = new CreateClaimRequest();
        badRequest.setAmount(new BigDecimal("-10.00")); // Invalid amount
        badRequest.setClaimType(null); // Invalid: null type

        // --- Act & Assert ---
        mockMvc.perform(post("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest))
                        // We still need to add the header, or it will fail
                        // for the wrong reason (Missing Header).
                        .header("X-User-Id", MOCK_USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getClaimsForUser_Fails_WhenHeaderIsMissing() throws Exception {
        // --- Act & Assert ---
        // Perform a GET request *without* the "X-User-Id" header
        // This proves our endpoint is protected by the header requirement.
        mockMvc.perform(get("/api/v1/claims"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request
    }
}