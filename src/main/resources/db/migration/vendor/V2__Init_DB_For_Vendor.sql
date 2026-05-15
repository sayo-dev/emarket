CREATE TABLE vendor_payouts
(
    id BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    deleted      BOOLEAN   NOT NULL DEFAULT FALSE,
    vendor_id    VARCHAR(255),
    amount       DECIMAL(19, 2),
    status       VARCHAR(255)       DEFAULT 'PENDING',
    reference    VARCHAR(255) UNIQUE,
    processed_at TIMESTAMP,
    CONSTRAINT fk_vendor_payouts_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id)
);
