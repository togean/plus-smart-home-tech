DROP TABLE IF EXISTS warehouse_products, booking_order, booking_products;

CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id       UUID PRIMARY KEY,
    fragile          BOOLEAN,
    width            DOUBLE PRECISION,
    height           DOUBLE PRECISION,
    depth            DOUBLE PRECISION,
    weight           DOUBLE PRECISION,
    quantity         INTEGER
);

create table if not exists booking_order
(
    booking_id       UUID PRIMARY KEY,
    order_id         UUID NOT NULL,
    delivery_id      UUID NOT NULL,
    delivery_weight  DOUBLE PRECISION NOT NULL,
    delivery_volume  DOUBLE PRECISION NOT NULL,
    fragile          BOOLEAN NOT NULL
);

create table if not exists booking_products
(
    booking_id UUID PRIMARY KEY,
    product_id       UUID NOT NULL,
    quantity         BIGINT
);