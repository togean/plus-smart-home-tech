drop table if exists shopping_carts, cart_products;

CREATE TABLE IF NOT EXISTS shopping_carts
(
    cart_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR NOT NULL,
    active BOOlEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products
(
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    cart_id UUID REFERENCES shopping_carts (cart_id) ON delete CASCADE
);