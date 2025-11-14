package com.ucrmp.authservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ucrmp.authservice.entity.Role;
import com.ucrmp.authservice.entity.User;
import com.ucrmp.authservice.repository.RoleRepository;
import com.ucrmp.authservice.repository.UserRepository;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    // --- NEW: Inject all required tools ---
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --- NEW: Read admin credentials from application.properties ---
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    // --- NEW: Updated Constructor ---
    public DataSeeder(RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- Step 1: Seed Roles ---
        if (roleRepository.count() == 0) {
            log.info("Roles table is empty. Seeding default roles...");
            Role adminRole = new Role("ROLE_ADMIN");
            Role employeeRole = new Role("ROLE_EMPLOYEE");
            roleRepository.save(adminRole);
            roleRepository.save(employeeRole);
            log.info("Default roles seeded.");
        } else {
            log.info("Roles table already populated. Skipping role seeding.");
        }

        // --- Step 2: Seed First Admin User ---
        if (userRepository.count() == 0) {
            log.info("Users table is empty. Creating initial admin user...");

            // Find the admin role we just created
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("CRITICAL ERROR: ROLE_ADMIN not found after seeding."));

            // Create the new admin user
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail(adminEmail);
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            adminUser.setRoles(Set.of(adminRole)); // Assign the admin role

            userRepository.save(adminUser);
            log.info("Initial admin user created successfully with email: {}", adminEmail);
        } else {
            log.info("Users table not empty. Skipping admin user creation.");
        }
    }
}