CREATE TABLE code (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120),
    description VARCHAR(500),
    value_code NUMERIC(3,1),
    discount_code NUMERIC(3,1),
    visibility VARCHAR(20),
    is_active BOOLEAN NOT NULL,
    is_used BOOLEAN NOT NULL,
    expiration DATE NOT NULL,
    created_at DATE NOT NULL
);

CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    birthday_date DATE NOT NULL,

    code_id BIGINT,

    CONSTRAINT fk_client_code
        FOREIGN KEY (code_id)
        REFERENCES code(id)
);