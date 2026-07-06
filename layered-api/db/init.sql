CREATE TABLE IF NOT EXISTS products(
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price NUMERIC(10,2) NOT NULL
);
INSERT INTO products(name, price) VALUES ('Teclado', 29.90), ('Mouse', 14.50);
