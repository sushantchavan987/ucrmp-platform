package com.ucrmp.claimservice.entity;

import com.ucrmp.claimservice.model.ClaimStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claim_status_history")
public class ClaimStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Column(name = "changed_at", updatable = false, nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by_id", nullable = false)
    private UUID changedById; // ID of the Employee or Manager

    @Column(columnDefinition = "TEXT")
    private String comment;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    // --- Constructors ---
    public ClaimStatusHistory() {
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    public UUID getChangedById() { return changedById; }
    public void setChangedById(UUID changedById) { this.changedById = changedById; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}