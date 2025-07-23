DROP TABLE IF EXISTS address, delivery;

CREATE TABLE IF NOT EXISTS address (
    address_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    country    VARCHAR NOT NULL,
    city       VARCHAR NOT NULL,
    street     VARCHAR NOT NULL,
    house      VARCHAR NOT NULL,
    flat       VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS delivery (
    delivery_id      UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id         UUID NOT NULL,
    from_address_id  UUID NOT NULL,
    to_address_id    UUID NOT NULL,
    status           VARCHAR NOT NULL
);