CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    category VARCHAR(255),
    stock INT,
    created_date TIMESTAMP NOT NULL,
    last_updated_date TIMESTAMP
);