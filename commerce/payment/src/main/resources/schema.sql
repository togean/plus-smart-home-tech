DROP TABLE IF EXISTS payments;

CREATE TABLE IF NOT EXISTS payments (
    payment_id       UUID PRIMARY KEY,
    order_id         UUID NOT NULL,
    payment_total    DOUBLE PRECISION,
    delivery_total   DOUBLE PRECISION,
    fee_total        DOUBLE PRECISION,
    payment_status   VARCHAR(50)
);