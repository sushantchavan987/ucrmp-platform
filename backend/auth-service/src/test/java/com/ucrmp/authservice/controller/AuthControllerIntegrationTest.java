package com.ucrmp.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucrmp.authservice.dto.RegisterRequest;
import com.ucrmp.authservice.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// --- The "Test Track" Setup ---
@Testcontainers // Enables Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Loads the whole app
@AutoConfigureMockMvc // Gives us MockMvc to make fake HTTP requests
class AuthControllerIntegrationTest {

    // This will start a new MySQL 8.0 Docker container for every test run
    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.28");

    // This "redirects" Spring Boot to use our test database, not the real one
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        // We set ddl-auto to 'create' to build the schema in the test DB
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    private MockMvc mockMvc; // Our tool for making fake HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // For converting Java objects to JSON

    @Autowired
    private UserRepository userRepository; // To check the database directly

    @Test
    void registerUser_Success_CreatesUserInDatabase() throws Exception {
        // 1. ARRANGE
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("integration@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Integration");
        registerRequest.setLastName("Test");

        // Convert the Java object to a JSON string
        String requestJson = objectMapper.writeValueAsString(registerRequest);

        // 2. ACT
        // Perform an actual POST request to our /register endpoint
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
        // 3. ASSERT (The HTTP Response)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("User registered successfully!"));

        // 4. ASSERT (The Database)
        // Go directly to the database and check if the user was created
        boolean userExists = userRepository.findByEmail("integration@example.com").isPresent();
        assertTrue(userExists, "User was not created in the database");
    }

    // You would add another test here for login
}