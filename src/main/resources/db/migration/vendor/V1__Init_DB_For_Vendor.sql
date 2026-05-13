CREATE TABLE products
(
    id BIGSERIAL PRIMARY KEY,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,
    deleted        BOOLEAN   NOT NULL DEFAULT FALSE,
    vendor_id      VARCHAR(255),
    category_id    BIGINT,
    name           VARCHAR(255),
    description    TEXT,
    base_price     DECIMAL(19, 2),
    product_status VARCHAR(255)       DEFAULT 'DRAFT',
    CONSTRAINT fk_products_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE product_images
(
    id            BIGSERIAL PRIMARY KEY,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    deleted       BOOLEAN   NOT NULL DEFAULT FALSE,
    product_id    BIGINT,
    image_url     VARCHAR(255),
    is_primary    BOOLEAN            DEFAULT FALSE,
    display_order INTEGER,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE product_variants
(
    id BIGSERIAL PRIMARY KEY,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,
    deleted        BOOLEAN   NOT NULL DEFAULT FALSE,
    product_id     BIGINT,
    name           VARCHAR(255),
    sku            VARCHAR(255) UNIQUE,
    price_modifier DECIMAL(19, 2),
    stock_quantity INTEGER,
    reserved_quantity INTEGER,
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products (id)
);

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

CREATE TABLE orders
(
    id BIGSERIAL PRIMARY KEY,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP,
    deleted             BOOLEAN   NOT NULL DEFAULT FALSE,
    customer_id         VARCHAR(255),
    status              VARCHAR(255)       DEFAULT 'PENDING_PAYMENT',
    subtotal            DECIMAL(19, 2),
    platform_commission DECIMAL(19, 2),
    vendor_earnings     DECIMAL(19, 2),
    shipping_fee        DECIMAL(19, 2),
    total               DECIMAL(19, 2),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users (id)
);

CREATE TABLE order_items
(
    id BIGSERIAL PRIMARY KEY,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP,
    deleted            BOOLEAN   NOT NULL DEFAULT FALSE,
    order_id           BIGINT,
    vendor_id          VARCHAR(255),
    product_variant_id BIGINT,
    quantity           INTEGER,
    unit_price         DECIMAL(19, 2),
    item_status        VARCHAR(255)       DEFAULT 'PROCESSING',
    shipped_at         TIMESTAMP,
    delivered_at       TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_items_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id),
    CONSTRAINT fk_order_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants (id)
);

CREATE TABLE shipping_info
(
    id BIGSERIAL PRIMARY KEY,
    created_at            TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP,
    deleted               BOOLEAN   NOT NULL DEFAULT FALSE,
    order_id              BIGINT UNIQUE,
    recipient_name        VARCHAR(255),
    address               VARCHAR(255),
    city                  VARCHAR(255),
    state                 VARCHAR(255),
    postal_code           VARCHAR(255),
    tracking_number       VARCHAR(255),
    courier               VARCHAR(255),
    estimated_delivery_at TIMESTAMP,
    actual_delivery_at    TIMESTAMP,
    CONSTRAINT fk_shipping_info_order FOREIGN KEY (order_id) REFERENCES orders (id)
);

CREATE TABLE carts
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted    BOOLEAN   NOT NULL DEFAULT FALSE,
    status     VARCHAR(255)       DEFAULT 'ACTIVE'
);

CREATE TABLE cart_items
(
    id BIGSERIAL PRIMARY KEY,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP,
    deleted            BOOLEAN   NOT NULL DEFAULT FALSE,
    cart_id            BIGINT,
    product_variant_id BIGINT,
    quantity           INTEGER,
    unit_price         DECIMAL(19, 2),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id),
    CONSTRAINT fk_cart_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants (id)
);

CREATE TABLE reviews
(
    id BIGSERIAL PRIMARY KEY,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP,
    deleted              BOOLEAN   NOT NULL DEFAULT FALSE,
    product_id           BIGINT,
    customer_id          VARCHAR(255),
    order_item_id        BIGINT,
    rating               INTEGER,
    title                VARCHAR(255),
    body                 TEXT,
    is_verified_purchase BOOLEAN            DEFAULT TRUE,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id) REFERENCES users (id),
    CONSTRAINT fk_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items (id)
);

CREATE TABLE disputes
(
    id BIGSERIAL PRIMARY KEY,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP,
    deleted           BOOLEAN   NOT NULL DEFAULT FALSE,
    order_id          BIGINT,
    raised_by_user_id VARCHAR(255),
    vendor_id         VARCHAR(255),
    reason            VARCHAR(255),
    description       TEXT,
    resolution_notes  TEXT,
    status            VARCHAR(255)       DEFAULT 'OPEN',
    resolved_by_id    VARCHAR(255),
    resolved_at       TIMESTAMP,
    CONSTRAINT fk_disputes_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_disputes_user FOREIGN KEY (raised_by_user_id) REFERENCES users (id),
    CONSTRAINT fk_disputes_vendor FOREIGN KEY (vendor_id) REFERENCES vendors (id),
    CONSTRAINT fk_disputes_resolved_by FOREIGN KEY (resolved_by_id) REFERENCES users (id)
);

