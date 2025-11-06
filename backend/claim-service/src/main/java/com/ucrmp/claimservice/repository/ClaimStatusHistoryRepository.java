package com.ucrmp.claimservice.repository;

import com.ucrmp.claimservice.entity.ClaimStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, UUID> {
}