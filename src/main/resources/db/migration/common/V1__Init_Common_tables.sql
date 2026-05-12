CREATE TABLE vendors
(
    id                       VARCHAR(255) PRIMARY KEY,
    business_name            VARCHAR(255),
    business_email           VARCHAR(255) NOT NULL UNIQUE,
    phone                    VARCHAR(255),
    bank_account_number      VARCHAR(255),
    bank_name                VARCHAR(255),
    status                   VARCHAR(255)   DEFAULT 'PENDING',
    total_earnings           DECIMAL(19, 2) DEFAULT 0.00,
    available_payout_balance DECIMAL(19, 2) DEFAULT 0.00,
    created_at               TIMESTAMP    NOT NULL,
    deleted_at               TIMESTAMP
);

CREATE TABLE users
(
    id           VARCHAR(255) PRIMARY KEY,
    vendor_id    VARCHAR(255),
    name         VARCHAR(255),
    email        VARCHAR(255) UNIQUE,
    password     VARCHAR(255),
    account_type VARCHAR(255),
    created_at   TIMESTAMP NOT NULL,
    is_verified  BOOLEAN DEFAULT FALSE,
    deleted_at   TIMESTAMP,
    CONSTRAINT fk_users_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id)
);

CREATE TABLE platform_config
(
    id                      INTEGER PRIMARY KEY DEFAULT 1,
    commission_rate_percent DECIMAL(19, 2)      DEFAULT 10.00,
    min_payout_threshold    DECIMAL(19, 2)      DEFAULT 2000.00,
    payout_schedule         VARCHAR(255)        DEFAULT 'MONTHLY',
    updated_at              TIMESTAMP           DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs
(
    id          BIGINT PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    deleted     BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id     VARCHAR(255),
    action      VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id   BIGINT,
    metadata JSONB,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
);
