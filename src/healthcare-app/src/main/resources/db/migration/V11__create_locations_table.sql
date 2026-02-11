-- V11__create_locations_table.sql
-- Location management for healthcare facilities (SB018)

CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    location_type VARCHAR(30) NOT NULL,
    
    -- Address fields (embedded value object)
    street_address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    
    -- Contact information
    phone_number VARCHAR(20),
    email VARCHAR(100),
    
    -- Operating hours (JSON or text)
    operating_hours VARCHAR(500),
    
    -- Capacity
    capacity INTEGER,
    
    -- Status
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_location_type CHECK (
        location_type IN (
            'HOSPITAL', 'CLINIC', 'URGENT_CARE', 'LABORATORY',
            'PHARMACY', 'IMAGING_CENTER', 'SURGERY_CENTER', 'REHABILITATION'
        )
    ),
    CONSTRAINT chk_capacity CHECK (capacity IS NULL OR capacity >= 0)
);

-- Indexes for performance (PF002)
CREATE INDEX idx_location_code ON locations(location_code);
CREATE INDEX idx_location_type ON locations(location_type) WHERE is_active = TRUE;
CREATE INDEX idx_location_active ON locations(is_active);

-- Insert sample locations
INSERT INTO locations (location_code, name, location_type, street_address, city, state, postal_code, country, phone_number, capacity, is_active)
VALUES
    ('LOC001', 'Main Hospital', 'HOSPITAL', '123 Healthcare Ave', 'New York', 'NY', '10001', 'USA', '555-0100', 500, TRUE),
    ('LOC002', 'Downtown Clinic', 'CLINIC', '456 Medical St', 'New York', 'NY', '10002', 'USA', '555-0101', 50, TRUE),
    ('LOC003', 'Urgent Care Center', 'URGENT_CARE', '789 Emergency Rd', 'Brooklyn', 'NY', '11201', 'USA', '555-0102', 30, TRUE),
    ('LOC004', 'Diagnostic Laboratory', 'LABORATORY', '321 Lab Lane', 'Manhattan', 'NY', '10003', 'USA', '555-0103', NULL, TRUE);
