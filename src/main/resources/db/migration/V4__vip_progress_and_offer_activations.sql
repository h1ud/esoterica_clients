ALTER TABLE clients
    ADD COLUMN weekly_product_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN vip BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN vip_since DATE;

CREATE TABLE store_settings (
    id BIGINT PRIMARY KEY,
    vip_required_products INTEGER NOT NULL,
    updated_at DATE NOT NULL
);

INSERT INTO store_settings (id, vip_required_products, updated_at)
VALUES (1, 10, CURRENT_DATE);

CREATE TABLE code_activation (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    code_id BIGINT NOT NULL,
    product_quantity INTEGER NOT NULL,
    activated_at DATE NOT NULL,

    CONSTRAINT fk_code_activation_client
        FOREIGN KEY (client_id)
        REFERENCES clients(id),

    CONSTRAINT fk_code_activation_code
        FOREIGN KEY (code_id)
        REFERENCES code(id)
);

UPDATE code
SET is_active = TRUE,
    is_used = FALSE
WHERE title IN ('20% OFF Cheesecakes', '2x1 Fridays', 'Combo Dulce Místico');
