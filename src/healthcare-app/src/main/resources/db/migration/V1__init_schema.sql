-- =============================================
-- V1__init_schema.sql
-- Healthcare Platform Initial Schema
-- All entities use UUID primary keys (GenerationType.UUID)
-- =============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- AUDIT SCHEMA (HIPAA Compliance - SEC011, SEC012)
-- =============================================

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL DEFAULT gen_random_uuid(),
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_id UUID,
    actor_username VARCHAR(255),
    actor_role VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    old_values JSONB,
    new_values JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_actor ON audit_log(actor_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
CREATE INDEX idx_audit_log_event_id ON audit_log(event_id);

-- =============================================
-- PATIENT MODULE
-- =============================================

CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medical_record_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    blood_type VARCHAR(10),
    
    -- Contact Information
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    secondary_phone VARCHAR(20),
    
    -- PII (encrypted at application level - SEC007)
    ssn VARCHAR(11),
    
    -- Address (embedded value object)
    address_street VARCHAR(255),
    address_city VARCHAR(100),
    address_state VARCHAR(50),
    address_zip_code VARCHAR(20),
    address_country VARCHAR(50) DEFAULT 'US',
    
    -- Insurance (embedded value object)
    insurance_provider VARCHAR(200),
    insurance_policy_number VARCHAR(100),
    insurance_group_number VARCHAR(100),
    insurance_holder_name VARCHAR(200),
    insurance_holder_relationship VARCHAR(50),
    insurance_effective_date DATE,
    insurance_expiration_date DATE,
    
    -- Emergency Contact (embedded value object)
    emergency_contact_name VARCHAR(200),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_email VARCHAR(255),
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    -- Audit fields (BaseEntity)
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Indexes for common queries (PF002)
CREATE INDEX idx_patient_mrn ON patients(medical_record_number);
CREATE INDEX idx_patient_email ON patients(email);
CREATE INDEX idx_patient_phone ON patients(phone_number);
CREATE INDEX idx_patient_name ON patients(last_name, first_name);
CREATE INDEX idx_patient_status ON patients(status);
CREATE INDEX idx_patient_not_deleted ON patients(is_deleted) WHERE is_deleted = false;

-- =============================================
-- PROVIDER MODULE (formerly Doctor)
-- =============================================

CREATE TABLE providers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    
    -- Professional Information
    provider_type VARCHAR(50) NOT NULL, -- DOCTOR, NURSE, SPECIALIST, etc.
    specialization VARCHAR(100),
    license_number VARCHAR(100) UNIQUE NOT NULL,
    license_state VARCHAR(50) NOT NULL,
    license_expiry DATE NOT NULL,
    npi_number VARCHAR(10), -- National Provider Identifier
    
    -- Qualifications
    qualification TEXT,
    years_of_experience INT,
    
    -- Practice Information
    consultation_fee DECIMAL(10, 2),
    is_accepting_patients BOOLEAN DEFAULT TRUE,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    
    -- Audit fields
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_provider_number ON providers(provider_number);
CREATE INDEX idx_provider_type ON providers(provider_type);
CREATE INDEX idx_provider_specialization ON providers(specialization);
CREATE INDEX idx_provider_name ON providers(last_name, first_name);
CREATE INDEX idx_provider_accepting ON providers(is_accepting_patients) WHERE is_accepting_patients = true;
CREATE INDEX idx_provider_not_deleted ON providers(is_deleted) WHERE is_deleted = false;

-- Provider Schedule
CREATE TABLE provider_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT DEFAULT 30,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Audit fields (inherited from BaseEntity)
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT uk_provider_schedule_day UNIQUE (provider_id, day_of_week)
);

CREATE INDEX idx_provider_schedule_provider ON provider_schedules(provider_id);
CREATE INDEX idx_provider_schedule_active ON provider_schedules(is_active) WHERE is_active = true;

-- =============================================
-- APPOINTMENT MODULE
-- =============================================

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- References
    patient_id UUID NOT NULL REFERENCES patients(id),
    provider_id UUID NOT NULL REFERENCES providers(id),
    
    -- Scheduling
    scheduled_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    
    -- Details
    appointment_type VARCHAR(50) NOT NULL, -- CONSULTATION, FOLLOW_UP, EMERGENCY, etc.
    reason_for_visit TEXT,
    notes TEXT,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    
    -- Cancellation
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancelled_by VARCHAR(255),
    cancelled_by_patient BOOLEAN DEFAULT FALSE,
    cancellation_reason TEXT,
    
    -- Check-in
    checked_in_at TIMESTAMP WITH TIME ZONE,
    check_in_notes TEXT,
    checked_out_at TIMESTAMP WITH TIME ZONE,
    
    -- Completion
    completed_at TIMESTAMP WITH TIME ZONE,
    completion_notes TEXT,
    
    -- Audit fields
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_appointment_number ON appointments(appointment_number);
CREATE INDEX idx_appointment_patient ON appointments(patient_id);
CREATE INDEX idx_appointment_provider ON appointments(provider_id);
CREATE INDEX idx_appointment_date ON appointments(scheduled_date);
CREATE INDEX idx_appointment_status ON appointments(status);
CREATE INDEX idx_appointment_provider_date ON appointments(provider_id, scheduled_date);
CREATE INDEX idx_appointment_patient_date ON appointments(patient_id, scheduled_date);

-- =============================================
-- MEDICAL RECORD MODULE
-- =============================================

CREATE TABLE medical_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    record_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- References
    patient_id UUID NOT NULL REFERENCES patients(id),
    provider_id UUID NOT NULL REFERENCES providers(id),
    appointment_id UUID REFERENCES appointments(id),
    
    -- Record Details
    record_type VARCHAR(50) NOT NULL, -- VISIT_NOTE, LAB_RESULT, DIAGNOSIS, etc.
    record_date TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Clinical Data (stored as JSONB for flexibility)
    clinical_data JSONB,
    
    -- Summary
    chief_complaint TEXT,
    diagnosis_codes TEXT[], -- ICD-10 codes
    notes TEXT,
    
    -- VitalSigns (embedded value object)
    systolic_bp INT,
    diastolic_bp INT,
    heart_rate INT,
    respiratory_rate INT,
    temperature DECIMAL(4, 1),
    oxygen_saturation INT,
    weight_kg DECIMAL(5, 2),
    height_cm DECIMAL(5, 1),
    pain_level INT,
    vitals_recorded_at TIMESTAMP WITH TIME ZONE,
    
    -- SoapNote (embedded value object)
    soap_subjective TEXT,
    soap_objective TEXT,
    soap_assessment TEXT,
    soap_plan TEXT,
    
    -- Attachments
    attachments_count INT DEFAULT 0,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    finalized_at TIMESTAMP WITH TIME ZONE,
    finalized_by VARCHAR(255),
    
    -- Audit fields
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_medical_record_number ON medical_records(record_number);
CREATE INDEX idx_medical_record_patient ON medical_records(patient_id);
CREATE INDEX idx_medical_record_provider ON medical_records(provider_id);
CREATE INDEX idx_medical_record_appointment ON medical_records(appointment_id);
CREATE INDEX idx_medical_record_date ON medical_records(record_date);
CREATE INDEX idx_medical_record_type ON medical_records(record_type);

-- Medical Record Diagnoses (ElementCollection for MedicalRecord)
CREATE TABLE medical_record_diagnoses (
    medical_record_id UUID NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,
    diagnosis_code VARCHAR(10),
    diagnosis_description VARCHAR(500),
    diagnosis_type VARCHAR(30),
    is_primary BOOLEAN,
    onset_date DATE,
    resolved_date DATE,
    diagnosis_notes VARCHAR(1000)
);

CREATE INDEX idx_diagnosis_medical_record ON medical_record_diagnoses(medical_record_id);

-- Patient Allergies (separate table for list relationship)
CREATE TABLE patient_allergies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    allergen VARCHAR(200) NOT NULL,
    allergen_type VARCHAR(50) NOT NULL, -- DRUG, FOOD, ENVIRONMENTAL, etc.
    severity VARCHAR(20) NOT NULL, -- MILD, MODERATE, SEVERE, LIFE_THREATENING
    reaction TEXT,
    onset_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_patient_allergy_patient ON patient_allergies(patient_id);
CREATE INDEX idx_patient_allergy_active ON patient_allergies(is_active) WHERE is_active = true;

-- =============================================
-- BILLING MODULE
-- =============================================

CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- References
    patient_id UUID NOT NULL REFERENCES patients(id),
    appointment_id UUID REFERENCES appointments(id),
    
    -- Amounts
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL,
    paid_amount DECIMAL(12, 2) DEFAULT 0,
    balance_due DECIMAL(12, 2) NOT NULL,
    
    -- Dates
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    paid_date DATE,
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    -- Insurance
    insurance_claim_number VARCHAR(100),
    insurance_amount DECIMAL(12, 2) DEFAULT 0,
    
    -- Notes
    notes TEXT,
    
    -- Audit fields
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_invoice_number ON invoices(invoice_number);
CREATE INDEX idx_invoice_patient ON invoices(patient_id);
CREATE INDEX idx_invoice_appointment ON invoices(appointment_id);
CREATE INDEX idx_invoice_status ON invoices(status);
CREATE INDEX idx_invoice_due_date ON invoices(due_date);

-- Invoice Line Items
CREATE TABLE invoice_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    description VARCHAR(500) NOT NULL,
    procedure_code VARCHAR(20), -- CPT code
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_invoice_item_invoice ON invoice_items(invoice_id);

-- =============================================
-- =============================================
-- AUTH MODULE (SEC004 - RBAC)
-- =============================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    
    -- Profile
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    
    -- Linked entities (optional - user can be linked to patient or provider)
    patient_id UUID REFERENCES patients(id),
    provider_id UUID REFERENCES providers(id),
    
    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP WITH TIME ZONE,
    
    -- Security
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip VARCHAR(45),
    password_changed_at TIMESTAMP WITH TIME ZONE,
    must_change_password BOOLEAN DEFAULT FALSE,
    
    -- MFA (SEC003)
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    
    -- Audit fields
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);
CREATE INDEX idx_user_patient ON users(patient_id) WHERE patient_id IS NOT NULL;
CREATE INDEX idx_user_provider ON users(provider_id) WHERE provider_id IS NOT NULL;

-- Roles (SEC004 - healthcare roles)
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_system_role BOOLEAN DEFAULT FALSE,
    
    -- Audit fields (inherited from BaseEntity)
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- User Roles (many-to-many)
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    assigned_by VARCHAR(255),
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_role_user ON user_roles(user_id);
CREATE INDEX idx_user_role_role ON user_roles(role_id);

-- Permissions (fine-grained access control)
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    
    -- Audit fields (inherited from BaseEntity)
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT uk_permission_resource_action UNIQUE (resource, action)
);

CREATE INDEX idx_permission_resource ON permissions(resource);

-- Role Permissions (many-to-many)
CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permission_role ON role_permissions(role_id);

-- Refresh Tokens (SB022 - JWT refresh tokens)
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    user_agent TEXT,
    ip_address VARCHAR(45),
    
    -- Audit fields (inherited from BaseEntity)
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_token_expires ON refresh_tokens(expires_at) WHERE revoked = false;

-- =============================================
-- NOTIFICATION MODULE
-- =============================================

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Recipient
    user_id UUID REFERENCES users(id),
    patient_id UUID REFERENCES patients(id),
    
    -- Content
    type VARCHAR(50) NOT NULL, -- EMAIL, SMS, PUSH, IN_APP
    category VARCHAR(50) NOT NULL, -- APPOINTMENT_REMINDER, LAB_RESULT, etc.
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    
    -- Delivery
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    read_at TIMESTAMP WITH TIME ZONE,
    failed_at TIMESTAMP WITH TIME ZONE,
    failure_reason TEXT,
    retry_count INT DEFAULT 0,
    
    -- Metadata
    metadata JSONB,
    
    -- Audit
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_notification_user ON notifications(user_id);
CREATE INDEX idx_notification_patient ON notifications(patient_id);
CREATE INDEX idx_notification_status ON notifications(status);
CREATE INDEX idx_notification_scheduled ON notifications(scheduled_at) WHERE status = 'PENDING';

-- Notification Preferences
CREATE TABLE notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_hours_start INT,
    quiet_hours_end INT,
    timezone VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_pref_user ON notification_preferences(user_id);

-- Notification Preference Categories (ElementCollection)
CREATE TABLE notification_preference_categories (
    preference_id UUID NOT NULL REFERENCES notification_preferences(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL
);

-- Notification Preference Muted Categories (ElementCollection)
CREATE TABLE notification_preference_muted (
    preference_id UUID NOT NULL REFERENCES notification_preferences(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL
);

-- Notification Templates
CREATE TABLE notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    subject_template VARCHAR(255),
    body_template TEXT NOT NULL,
    html_template TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    locale VARCHAR(10),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_template_code ON notification_templates(template_code);
CREATE INDEX idx_template_category_type ON notification_templates(category, type);

-- =============================================
-- SEED DATA
-- =============================================

-- Default Roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN', 'System administrator with full access'),
    ('ROLE_DOCTOR', 'Healthcare provider - doctor'),
    ('ROLE_NURSE', 'Healthcare provider - nurse'),
    ('ROLE_RECEPTIONIST', 'Front desk staff'),
    ('ROLE_BILLING', 'Billing department staff'),
    ('ROLE_PATIENT', 'Patient user');

-- Default Permissions
INSERT INTO permissions (name, resource, action, description) VALUES
    ('patient:read', 'patient', 'read', 'View patient records'),
    ('patient:write', 'patient', 'write', 'Create/update patient records'),
    ('patient:delete', 'patient', 'delete', 'Delete patient records'),
    ('provider:read', 'provider', 'read', 'View provider records'),
    ('provider:write', 'provider', 'write', 'Create/update provider records'),
    ('provider:delete', 'provider', 'delete', 'Delete provider records'),
    ('appointment:read', 'appointment', 'read', 'View appointments'),
    ('appointment:write', 'appointment', 'write', 'Schedule/modify appointments'),
    ('appointment:delete', 'appointment', 'delete', 'Cancel appointments'),
    ('medical_record:read', 'medical_record', 'read', 'View medical records'),
    ('medical_record:write', 'medical_record', 'write', 'Create/update medical records'),
    ('billing:read', 'billing', 'read', 'View invoices'),
    ('billing:write', 'billing', 'write', 'Create/process invoices'),
    ('user:read', 'user', 'read', 'View user accounts'),
    ('user:write', 'user', 'write', 'Manage user accounts'),
    ('audit:read', 'audit', 'read', 'View audit logs');

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ROLE_ADMIN';

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_DOCTOR' AND p.name IN (
    'patient:read', 'patient:write',
    'provider:read',
    'appointment:read', 'appointment:write',
    'medical_record:read', 'medical_record:write'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_NURSE' AND p.name IN (
    'patient:read', 'patient:write',
    'provider:read',
    'appointment:read', 'appointment:write',
    'medical_record:read'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_RECEPTIONIST' AND p.name IN (
    'patient:read', 'patient:write',
    'provider:read',
    'appointment:read', 'appointment:write'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.name = 'ROLE_BILLING' AND p.name IN (
    'patient:read',
    'appointment:read',
    'billing:read', 'billing:write'
);
