package com.ucrmp.authservice.service;

// Import all the necessary classes
import com.ucrmp.authservice.dto.RegisterRequest;
import com.ucrmp.authservice.entity.Role;
import com.ucrmp.authservice.entity.User;
import com.ucrmp.authservice.repository.RoleRepository;
import com.ucrmp.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// This tells JUnit to use the Mockito "simulator"
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    // --- The Mocks (The Simulator) ---

    // We're creating FAKE versions of these dependencies.
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    // --- The Class Under Test (The Pilot) ---

    // This tells Mockito: "Create a REAL AuthServiceImpl, but
    // inject all the @Mock objects above into its constructor."
    @InjectMocks
    private AuthServiceImpl authService;

    // We'll define a reusable request object
    private RegisterRequest registerRequest;
    private Role employeeRole;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Create a standard request for our tests
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        // Create a standard role for our tests
        employeeRole = new Role("ROLE_EMPLOYEE");
    }

    // --- The Tests ---
    
    @Test
    void registerUser_Success_WhenEmailIsNew() {
        // 1. ARRANGE (Set up the simulator)
        
        // "When anyone calls userRepository.findByEmail with ANY string,
        //  pretend the email is new and return an empty Optional."
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // "When anyone calls passwordEncoder.encode, just return a fake hash."
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        
        // "When anyone calls roleRepository.findByName for "ROLE_EMPLOYEE",
        //  return our fake employeeRole."
        when(roleRepository.findByName("ROLE_EMPLOYEE")).thenReturn(Optional.of(employeeRole));

        // 2. ACT (Run the code we are testing)
        // We call the real registerUser method.
        authService.registerUser(registerRequest);

        // 3. ASSERT (Check if the correct things happened)
        
        // "We verify that the userRepository's save method
        //  was called exactly 1 time with ANY User object."
        verify(userRepository, times(1)).save(any(User.class));
        
        // We can also verify that the password encoder was used.
        verify(passwordEncoder, times(1)).encode("password123");
    }
    
    @Test
    void registerUser_ThrowsException_WhenEmailExists() {
        // 1. ARRANGE (Set up the simulator)
        
        // "When anyone calls userRepository.findByEmail,
        //  pretend the email ALREADY EXISTS by returning a fake User."
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        // 2. ACT & ASSERT (Run the code and check for the error)
        
        // We are asserting that the code inside the lambda (->)
        // MUST throw a RuntimeException.
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registerRequest);
        });

        // 3. ASSERT (Check what *didn't* happen)
        
        // "We verify that the userRepository's save method
        //  was NEVER called." This is crucial.
        verify(userRepository, never()).save(any(User.class));
    }
}