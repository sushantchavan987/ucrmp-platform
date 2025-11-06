-- V1__init_claim_schema.sql
-- This file creates the initial schema for the claim-service

CREATE TABLE claims (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    claim_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE receipts (
    id BINARY(16) NOT NULL,
    claim_id BINARY(16) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(1024) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE
);

CREATE TABLE claim_status_history (
    id BINARY(16) NOT NULL,
    claim_id BINARY(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by_id BINARY(16) NOT NULL,
    comment TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE
);