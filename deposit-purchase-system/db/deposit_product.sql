CREATE TABLE IF NOT EXISTS deposit_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(64) NOT NULL UNIQUE,
    product_name VARCHAR(128) NOT NULL,
    interest_rate DECIMAL(10,4) NOT NULL,
    min_amount DECIMAL(16,2) NOT NULL,
    max_amount DECIMAL(16,2) NOT NULL,
    daily_limit DECIMAL(16,2) NOT NULL,
    single_limit DECIMAL(16,2) NOT NULL,
    risk_level VARCHAR(16) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    attributes JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_code ON deposit_product(product_code);

INSERT INTO deposit_product (product_code, product_name, interest_rate, min_amount, max_amount, daily_limit, single_limit, risk_level, status, attributes) VALUES
('FX001', '湘悦灵活存', 0.0320, 1000.00, 500000.00, 2000000.00, 500000.00, 'R2', 1, JSON_OBJECT('tenor', '180天', 'hot', true)),
('JD888', '湘悦极速存', 0.0450, 10000.00, 200000.00, 1000000.00, 200000.00, 'R3', 1, JSON_OBJECT('tenor', '90天', 'promo', '秒杀'));

CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(16,2) NOT NULL,
    flow_code VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_user ON purchase_order(user_id);
CREATE INDEX idx_purchase_product ON purchase_order(product_id);

CREATE TABLE IF NOT EXISTS idempotent_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    idempotent_id VARCHAR(255) NOT NULL UNIQUE,
    request_body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS async_task_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL UNIQUE,
    flow_code VARCHAR(64) NOT NULL,
    node_id VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    payload TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
