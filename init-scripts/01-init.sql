-- ============================================
-- FRAUD DETECTION SYSTEM - DATABASE SCHEMA
-- ============================================

-- Create schema
CREATE SCHEMA IF NOT EXISTS fraud;

-- Set search path
SET search_path TO fraud, public;

-- ============================================
-- TRANSACTIONS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    user_id VARCHAR(100) NOT NULL,
    merchant_id VARCHAR(100),
    merchant_name VARCHAR(255),
    merchant_category VARCHAR(100),
    location VARCHAR(255),
    ip_address VARCHAR(45),
    device_id VARCHAR(100),
    card_type VARCHAR(50),
    card_last_four VARCHAR(4),
    transaction_type VARCHAR(50),
    channel VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    is_fraud BOOLEAN DEFAULT FALSE,
    fraud_score DECIMAL(5, 4),
    fraud_reason TEXT,
    rules_triggered TEXT[],
    processing_time_ms INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE
);

-- ============================================
-- FRAUD ALERTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS fraud_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_id VARCHAR(100) NOT NULL UNIQUE,
    transaction_id VARCHAR(100) NOT NULL REFERENCES transactions(transaction_id),
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    fraud_score DECIMAL(5, 4),
    rules_triggered TEXT[],
    description TEXT,
    recommended_action TEXT,
    analyst_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'OPEN',
    resolution TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- ============================================
-- USER PROFILES TABLE (For Velocity Checks)
-- ============================================
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    phone VARCHAR(50),
    country VARCHAR(100),
    typical_transaction_amount DECIMAL(19, 4),
    last_known_ip VARCHAR(45),
    last_known_location VARCHAR(255),
    last_transaction_at TIMESTAMP WITH TIME ZONE,
    transaction_count_24h INTEGER DEFAULT 0,
    total_amount_24h DECIMAL(19, 4) DEFAULT 0,
    risk_score DECIMAL(5, 4) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- RULES CONFIGURATION TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS rules_config (
    id BIGSERIAL PRIMARY KEY,
    rule_id VARCHAR(100) NOT NULL UNIQUE,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    description TEXT,
    condition_expression TEXT,
    threshold_value DECIMAL(19, 4),
    severity VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- AUDIT LOG TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_value JSONB,
    new_value JSONB,
    performed_by VARCHAR(100),
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_merchant_id ON transactions(merchant_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_is_fraud ON transactions(is_fraud);
CREATE INDEX IF NOT EXISTS idx_fraud_alerts_transaction_id ON fraud_alerts(transaction_id);
CREATE INDEX IF NOT EXISTS idx_fraud_alerts_status ON fraud_alerts(status);
CREATE INDEX IF NOT EXISTS idx_fraud_alerts_severity ON fraud_alerts(severity);
CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles(user_id);

-- ============================================
-- INSERT DEFAULT RULES
-- ============================================
INSERT INTO rules_config (rule_id, rule_name, rule_type, description, threshold_value, severity, priority) VALUES
('RULE_001', 'High Amount Threshold', 'AMOUNT', 'Flag transactions exceeding $10,000', 10000.00, 'HIGH', 1),
('RULE_002', 'Very High Amount Threshold', 'AMOUNT', 'Flag transactions exceeding $50,000', 50000.00, 'CRITICAL', 1),
('RULE_003', 'Velocity Check - Count', 'VELOCITY', 'Flag more than 10 transactions in 1 hour', 10, 'MEDIUM', 2),
('RULE_004', 'Velocity Check - Amount', 'VELOCITY', 'Flag if total in 24h exceeds $25,000', 25000.00, 'HIGH', 2),
('RULE_005', 'Location Anomaly', 'LOCATION', 'Flag if transaction location differs from last known', NULL, 'MEDIUM', 3),
('RULE_006', 'IP Address Change', 'IP', 'Flag if IP address differs significantly from last known', NULL, 'LOW', 4),
('RULE_007', 'Midnight Transactions', 'TIME', 'Flag transactions between 1 AM and 5 AM local time', NULL, 'LOW', 5),
('RULE_008', 'International Transaction', 'LOCATION', 'Flag cross-border transactions', NULL, 'LOW', 6)
ON CONFLICT (rule_id) DO NOTHING;

-- ============================================
-- FUNCTIONS
-- ============================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers for updated_at
CREATE TRIGGER update_transactions_updated_at
    BEFORE UPDATE ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fraud_alerts_updated_at
    BEFORE UPDATE ON fraud_alerts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rules_config_updated_at
    BEFORE UPDATE ON rules_config
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- GRANT PERMISSIONS
-- ============================================
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA fraud TO fraud_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA fraud TO fraud_admin;
GRANT USAGE ON SCHEMA fraud TO fraud_admin;
