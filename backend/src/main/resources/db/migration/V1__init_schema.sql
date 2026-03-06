-- V1__init_schema.sql
-- Online Shop - initial database schema

CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE categories (
    id              BIGSERIAL       PRIMARY KEY,
    parent_id       BIGINT          REFERENCES categories(id) ON DELETE SET NULL,
    name            VARCHAR(150)    NOT NULL,
    slug            VARCHAR(150)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uq_categories_slug UNIQUE (slug)
);

CREATE INDEX idx_categories_parent_id ON categories(parent_id);

CREATE TABLE products (
    id              BIGSERIAL       PRIMARY KEY,
    category_id     BIGINT          NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    sku             VARCHAR(64)     NOT NULL,
    gtin14          CHAR(14),
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    price_cents     INTEGER         NOT NULL,
    currency        CHAR(3)         NOT NULL,
    stock_qty       INTEGER         NOT NULL DEFAULT 0,
    image_url       VARCHAR(500),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ,
    CONSTRAINT uq_products_sku UNIQUE (sku),
    CONSTRAINT chk_products_sku_format CHECK (sku ~ '^[A-Z0-9._\-]+$'),
    CONSTRAINT chk_products_gtin14_format CHECK (gtin14 IS NULL OR gtin14 ~ '^[0-9]{14}$'),
    CONSTRAINT chk_products_price_cents_non_negative CHECK (price_cents >= 0),
    CONSTRAINT chk_products_stock_qty_non_negative CHECK (stock_qty >= 0),
    CONSTRAINT chk_products_currency_upper CHECK (currency = UPPER(currency))
);

CREATE UNIQUE INDEX uq_products_gtin14 ON products(gtin14) WHERE gtin14 IS NOT NULL;
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_active ON products(active);

CREATE TABLE orders (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          REFERENCES users(id) ON DELETE RESTRICT,
    order_number        VARCHAR(32)     NOT NULL,
    contact_email       VARCHAR(255)    NOT NULL,
    contact_phone       VARCHAR(32),
    status              TEXT            NOT NULL,
    total_cents         INTEGER         NOT NULL,
    currency            CHAR(3)         NOT NULL,
    placed_at           TIMESTAMPTZ     NOT NULL DEFAULT now(),
    paid_at             TIMESTAMPTZ,
    shipping_name       VARCHAR(200)    NOT NULL,
    shipping_line1      VARCHAR(200)    NOT NULL,
    shipping_line2      VARCHAR(200),
    shipping_city       VARCHAR(120)    NOT NULL,
    shipping_state      VARCHAR(120),
    shipping_postal     VARCHAR(32)     NOT NULL,
    shipping_country    CHAR(2)         NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ,
    CONSTRAINT uq_orders_order_number UNIQUE (order_number),
    CONSTRAINT chk_orders_status CHECK (status IN ('NEW', 'PAID', 'SHIPPED', 'CANCELED', 'REFUNDED')),
    CONSTRAINT chk_orders_total_cents_non_negative CHECK (total_cents >= 0),
    CONSTRAINT chk_orders_currency_upper CHECK (currency = UPPER(currency)),
    CONSTRAINT chk_orders_shipping_country_format CHECK (shipping_country ~ '^[A-Z]{2}$')
);

CREATE INDEX idx_orders_user_id_placed_at ON orders(user_id, placed_at);
CREATE INDEX idx_orders_contact_email_placed_at ON orders(contact_email, placed_at);

CREATE TABLE order_items (
    id                  BIGSERIAL       PRIMARY KEY,
    order_id            BIGINT          NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id          BIGINT          NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    sku                 VARCHAR(64)     NOT NULL,
    product_name        VARCHAR(200)    NOT NULL,
    gtin14              CHAR(14),
    unit_price_cents    INTEGER         NOT NULL,
    quantity            INTEGER         NOT NULL,
    line_total_cents    INTEGER         NOT NULL,
    CONSTRAINT uq_order_items_order_product UNIQUE (order_id, product_id),
    CONSTRAINT chk_order_items_gtin14_format CHECK (gtin14 IS NULL OR gtin14 ~ '^[0-9]{14}$'),
    CONSTRAINT chk_order_items_unit_price_non_negative CHECK (unit_price_cents >= 0),
    CONSTRAINT chk_order_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_order_items_line_total CHECK (line_total_cents = unit_price_cents * quantity)
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);