package com.ucrmp.claimservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        
        // This tells the app not to try and contact Eureka during this test
        registry.add("eureka.client.enabled", () -> "false"); 
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
        // Clean up the database after each test
        claimRepository.deleteAll();
    }

    @Test
    void createClaim_Success() throws Exception {
        // --- Arrange ---
        String metadataJson = "{\"hotelName\":\"Test Hotel\",\"flightNumber\":\"TEST123\"}";
        
        String requestJson = String.format(
            """
            {
                "claimType": "TRAVEL",
                "amount": 120.50,
                "description": "Doctor visit",
                "metadata": %s
            }
            """,
            metadataJson
        );

        // --- Act ---
        mockMvc.perform(post("/api/v1/claims")
                        // THIS IS THE FIX: We simulate the gateway by adding the header
                        .header("X-User-Id", MOCK_USER_ID) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
        // --- Assert (The HTTP Response) ---
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(120.50)) // Numeric comparison
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.claimType").value("TRAVEL"))
                // --- THIS IS THE FIX for the brittle test ---
                // We test the *fields inside* the metadata, not the raw string
                .andExpect(jsonPath("$.metadata.hotelName").value("Test Hotel"))
                .andExpect(jsonPath("$.metadata.flightNumber").value("TEST123"));

        // --- Assert (The Database) ---
        assertEquals(1, claimRepository.findAll().size());
        assertEquals(UUID.fromString(MOCK_USER_ID), claimRepository.findAll().get(0).getUserId());
        
        // We can parse the JSON string from the DB to check it
        String dbMetadata = claimRepository.findAll().get(0).getMetadata();
        assertEquals("Test Hotel", objectMapper.readTree(dbMetadata).get("hotelName").asText());
    }

    @Test
    void createClaim_ValidationFails_ReturnsBadRequest() throws Exception {
        // --- Arrange ---
        // This request is invalid because 'metadata' is missing
        String badRequestJson = """
            {
                "claimType": "TRAVEL",
                "amount": 120.50,
                "description": "Doctor visit"
            }
            """;

        // --- Act & Assert ---
        mockMvc.perform(post("/api/v1/claims")
                        .header("X-User-Id", MOCK_USER_ID) // We still need the header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badRequestJson))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.message").exists()); 
    }

    @Test
    void getClaimsForUser_Fails_WhenHeaderIsMissing() throws Exception {
        // --- Act & Assert ---
        // We perform the request *without* the X-User-Id header
        mockMvc.perform(get("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON))
                // Spring correctly sees a required header is missing and returns 400
                .andExpect(status().isBadRequest());
    }
}