package com.ucrmp.authservice.service;

import com.ucrmp.authservice.dto.LoginRequest;
import com.ucrmp.authservice.dto.LoginResponse;
import com.ucrmp.authservice.dto.RegisterRequest;
import com.ucrmp.authservice.entity.Role;
import com.ucrmp.authservice.entity.User;
import com.ucrmp.authservice.repository.RoleRepository;
import com.ucrmp.authservice.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    // --- Fields ---
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // --- Constructor for Dependency Injection ---
    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user in the system.
     */
    @Override
    public void registerUser(RegisterRequest registerRequest) {
        // Step 1: Check if the user's email already exists.
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Step 2: Create a new User entity.
        User newUser = new User();
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());

        // Step 3: Hash the plain-text password before saving it.
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        // Step 4: Assign a default role ("ROLE_EMPLOYEE").
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
        
        Set<Role> roles = new HashSet<>();
        roles.add(employeeRole);
        newUser.setRoles(roles);

        // Step 5: Save the fully prepared User entity to the database.
        userRepository.save(newUser);
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        // Step 1: Authenticate the user.
        // This will use our UserDetailsService and PasswordEncoder.
        // It throws an exception if authentication fails.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Step 2: If authentication is successful, fetch the user.
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        // Step 3: Build a UserDetails object from our User entity.
        UserDetails userDetails = buildUserDetails(user);

        // Step 4: Generate a JWT token using the JwtService.
        String token = jwtService.generateToken(userDetails);

        // Step 5: Return the token in our LoginResponse DTO.
        return new LoginResponse(token);
    }

    /**
     * Helper method to convert our custom User entity into Spring Security's
     * UserDetails object.
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(
                    user.getRoles().stream()
                        .map(role -> role.getName())
                        .toArray(String[]::new)
                )
                .build();
    }
}