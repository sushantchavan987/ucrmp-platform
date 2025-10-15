package com.ucrmp.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ucrmp.authservice.entity.Role;
import com.ucrmp.authservice.repository.RoleRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    // The final field that needs to be initialized.
    private final RoleRepository roleRepository;

    // --- Constructor ---
    // Spring will use this constructor for Dependency Injection.
    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");

            Role employeeRole = new Role();
            employeeRole.setName("ROLE_EMPLOYEE");

            roleRepository.save(adminRole);
            roleRepository.save(employeeRole);

            System.out.println("✅ Default roles have been seeded to the database.");
        } else {
            System.out.println(" Roles table is not empty. Skipping seeding.");
        }
    }
}