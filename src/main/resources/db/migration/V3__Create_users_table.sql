CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    roles          VARCHAR(255) NOT NULL CHECK (roles IN ('USER', 'ADMIN')),
    PRIMARY KEY (user_id, roles),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);