-- V1: Create dog table for adoption data
-- This migration creates the basic dog table structure

CREATE TABLE dog (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    description TEXT
);

-- Create indexes for better query performance
CREATE INDEX idx_dog_name ON dog(name);
CREATE INDEX idx_dog_owner ON dog(owner); 