package com.ucrmp.claimservice.repository;

import com.ucrmp.claimservice.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
// This 'extends' part is what gives you the .save() method
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    
    // This will find all claims submitted by a specific user
    List<Claim> findByUserId(UUID userId);
}