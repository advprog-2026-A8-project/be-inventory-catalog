CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    jastiper_id VARCHAR(255) NOT NULL,
    origin_country VARCHAR(255) NOT NULL,
    purchase_date DATE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_product_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_product_jastiper_id ON products(jastiper_id);
