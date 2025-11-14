-- V2__add_metadata_to_claims.sql
-- Adds a flexible JSON column to store claim-specific dynamic fields
ALTER TABLE claims
ADD COLUMN metadata JSON;