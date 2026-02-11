-- V3: Create audit_events table for HIPAA-compliant audit logging
-- This table is IMMUTABLE - no UPDATE or DELETE operations should be performed
-- Retention: 6+ years per HIPAA requirements

CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY,
    
    -- Timestamp
    event_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Correlation
    correlation_id VARCHAR(64),
    session_id VARCHAR(64),
    
    -- Actor information
    user_id UUID NOT NULL,
    username VARCHAR(100),
    user_role VARCHAR(50),
    client_ip_hash VARCHAR(64),
    user_agent VARCHAR(255),
    
    -- Action information
    action VARCHAR(30) NOT NULL,
    outcome VARCHAR(20) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    
    -- Resource information
    resource_category VARCHAR(30) NOT NULL,
    resource_id UUID,
    resource_type VARCHAR(100),
    patient_id UUID,
    
    -- Change tracking
    accessed_fields TEXT,
    changed_fields TEXT,
    
    -- Request information
    http_method VARCHAR(10),
    request_uri VARCHAR(500),
    response_status INTEGER,
    response_time_ms BIGINT,
    
    -- Error information
    error_code VARCHAR(50),
    error_message VARCHAR(500),
    
    -- Metadata
    metadata TEXT,
    checksum VARCHAR(64),
    schema_version VARCHAR(10) DEFAULT '1.0'
);

-- Indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_audit_event_timestamp 
    ON audit_events(event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_user_id 
    ON audit_events(user_id, event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_resource 
    ON audit_events(resource_category, resource_id);

CREATE INDEX IF NOT EXISTS idx_audit_event_action 
    ON audit_events(action, event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_patient_id 
    ON audit_events(patient_id, event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_correlation_id 
    ON audit_events(correlation_id);

CREATE INDEX IF NOT EXISTS idx_audit_event_session_id 
    ON audit_events(session_id);

CREATE INDEX IF NOT EXISTS idx_audit_event_outcome 
    ON audit_events(outcome, event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_audit_event_severity 
    ON audit_events(severity, event_timestamp DESC);

-- Composite index for compliance reporting
CREATE INDEX IF NOT EXISTS idx_audit_event_compliance 
    ON audit_events(event_timestamp, resource_category, action, outcome);

-- Index for security event monitoring
CREATE INDEX IF NOT EXISTS idx_audit_event_security 
    ON audit_events(action, outcome, event_timestamp DESC)
    WHERE action IN ('LOGIN', 'LOGOUT', 'LOGIN_FAILED', 'ACCESS_DENIED', 
                     'PASSWORD_CHANGE', 'PASSWORD_RESET');

-- Partial index for PHI access events
CREATE INDEX IF NOT EXISTS idx_audit_event_phi 
    ON audit_events(event_timestamp, user_id, patient_id)
    WHERE resource_category IN ('PATIENT', 'MEDICAL_RECORD', 'PRESCRIPTION', 
                                 'LAB_RESULT', 'DIAGNOSIS', 'VITAL_SIGNS',
                                 'ALLERGY', 'IMMUNIZATION', 'BILLING', 'INSURANCE');

-- Comments for documentation
COMMENT ON TABLE audit_events IS 'HIPAA-compliant audit log - IMMUTABLE (no UPDATE/DELETE)';
COMMENT ON COLUMN audit_events.id IS 'Unique identifier for the audit event';
COMMENT ON COLUMN audit_events.event_timestamp IS 'UTC timestamp when event occurred';
COMMENT ON COLUMN audit_events.correlation_id IS 'Request correlation ID for tracing';
COMMENT ON COLUMN audit_events.user_id IS 'ID of user who performed the action';
COMMENT ON COLUMN audit_events.client_ip_hash IS 'Hashed client IP for privacy';
COMMENT ON COLUMN audit_events.action IS 'Type of action performed';
COMMENT ON COLUMN audit_events.outcome IS 'Result of the action (SUCCESS, FAILURE, DENIED, ERROR)';
COMMENT ON COLUMN audit_events.severity IS 'Severity level (LOW, MEDIUM, HIGH, CRITICAL)';
COMMENT ON COLUMN audit_events.resource_category IS 'Category of resource accessed';
COMMENT ON COLUMN audit_events.patient_id IS 'Patient ID for patient-centric audit trail';
COMMENT ON COLUMN audit_events.changed_fields IS 'JSON of field changes (values masked per SEC009)';
COMMENT ON COLUMN audit_events.checksum IS 'SHA-256 hash for tamper detection';
