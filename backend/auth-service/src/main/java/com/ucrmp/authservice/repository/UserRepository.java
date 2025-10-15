package com.ucrmp.authservice.repository;

import com.ucrmp.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Spring Data JPA will automatically create the query for this method
    // based on its name: "find by email"
    Optional<User> findByEmail(String email);
}