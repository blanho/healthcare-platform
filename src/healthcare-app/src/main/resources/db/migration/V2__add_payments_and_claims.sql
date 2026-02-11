-- V2__add_payments_and_claims.sql
-- Additional billing tables for payments and insurance claims

-- =====================================================
-- PAYMENTS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    patient_id UUID NOT NULL REFERENCES patients(id),
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    authorization_code VARCHAR(50),
    card_last_four VARCHAR(4),
    card_brand VARCHAR(20),
    failure_reason VARCHAR(500),
    notes TEXT,
    payment_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE,
    refund_amount DECIMAL(12, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    
    CONSTRAINT chk_payment_amount CHECK (amount > 0),
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED')),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'CHECK', 'BANK_TRANSFER', 'INSURANCE', 'HSA', 'FSA', 'PAYMENT_PLAN', 'WRITE_OFF'))
);

CREATE INDEX IF NOT EXISTS idx_payment_invoice ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payment_patient ON payments(patient_id);
CREATE INDEX IF NOT EXISTS idx_payment_reference ON payments(reference_number);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);

-- =====================================================
-- INSURANCE CLAIMS TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS insurance_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    patient_id UUID NOT NULL REFERENCES patients(id),
    
    -- Insurance details
    insurance_provider VARCHAR(255) NOT NULL,
    policy_number VARCHAR(50) NOT NULL,
    group_number VARCHAR(50),
    subscriber_name VARCHAR(255),
    subscriber_id VARCHAR(50),
    
    -- Amounts
    billed_amount DECIMAL(12, 2) NOT NULL,
    allowed_amount DECIMAL(12, 2),
    paid_amount DECIMAL(12, 2) DEFAULT 0,
    patient_responsibility DECIMAL(12, 2),
    copay_amount DECIMAL(12, 2),
    deductible_amount DECIMAL(12, 2),
    coinsurance_amount DECIMAL(12, 2),
    
    -- Status and dates
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP WITH TIME ZONE,
    processed_at TIMESTAMP WITH TIME ZONE,
    service_date DATE NOT NULL,
    
    -- Response details
    denial_reason VARCHAR(500),
    denial_code VARCHAR(20),
    adjudication_notes TEXT,
    eob_reference VARCHAR(100),
    
    -- Audit
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100),
    
    CONSTRAINT chk_claim_status CHECK (status IN ('DRAFT', 'SUBMITTED', 'ACKNOWLEDGED', 'IN_REVIEW', 'PENDING_INFO', 'APPROVED', 'PARTIALLY_APPROVED', 'DENIED', 'APPEALED', 'PAID', 'CLOSED'))
);

CREATE INDEX IF NOT EXISTS idx_claim_number ON insurance_claims(claim_number);
CREATE INDEX IF NOT EXISTS idx_claim_invoice ON insurance_claims(invoice_id);
CREATE INDEX IF NOT EXISTS idx_claim_patient ON insurance_claims(patient_id);
CREATE INDEX IF NOT EXISTS idx_claim_status ON insurance_claims(status);

-- =====================================================
-- ADDITIONAL BILLING PERMISSIONS
-- =====================================================
INSERT INTO permissions (id, name, resource, action, description) 
SELECT gen_random_uuid(), 'billing:refund', 'billing', 'refund', 'Permission to process refunds'
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'billing:refund');

INSERT INTO permissions (id, name, resource, action, description) 
SELECT gen_random_uuid(), 'billing:claim:write', 'billing', 'claim:write', 'Permission to manage insurance claims'
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'billing:claim:write');
