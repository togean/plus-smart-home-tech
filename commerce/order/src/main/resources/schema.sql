DROP TABLE IF EXISTS orders, order_items;

CREATE TABLE IF NOT EXISTS orders (
    order_id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    shoppingcart_id  UUID NOT NULL,
    username         VARCHAR NOT NULL,
    payment_id       UUID,
    delivery_id      UUID,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION,
    status           VARCHAR(50),
    fragile          BOOLEAN
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id   UUID REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity   INTEGER
);