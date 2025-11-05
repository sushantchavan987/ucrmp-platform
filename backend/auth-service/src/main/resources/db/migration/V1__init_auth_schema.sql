-- V1__init_auth_schema.sql
-- This file creates the initial schema for the auth-service

-- Create the roles table
CREATE TABLE roles (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

-- Create the users table
CREATE TABLE users (
    id BINARY(16) NOT NULL, -- UUID is stored as BINARY(16)
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- Create the user_roles join table
CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);