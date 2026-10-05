CREATE TABLE users (
    id            UUID PRIMARY KEY,
    name          VARCHAR(160) NOT NULL,
    email         VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(24)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_users_role ON users (role);
