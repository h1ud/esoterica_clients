CREATE TABLE menu_item (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(80) NOT NULL,
    price NUMERIC(8,2) NOT NULL,
    image_url VARCHAR(600),
    is_available BOOLEAN NOT NULL,
    is_featured BOOLEAN NOT NULL,
    created_at DATE NOT NULL
);

INSERT INTO menu_item (title, description, category, price, image_url, is_available, is_featured, created_at) VALUES
('Torta Luna de Vainilla', 'Bizcocho suave con crema de vainilla, brillo perlado y flores de azúcar.', 'Tortas', 48.00, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=900&q=80', TRUE, TRUE, CURRENT_DATE),
('Brownie Eclipse', 'Brownie oscuro con cacao intenso, nueces tostadas y lluvia de sal encantada.', 'Postres', 12.00, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=900&q=80', TRUE, TRUE, CURRENT_DATE),
('Cheesecake Aurora', 'Cheesecake cremoso con frutos rojos y un espejo de fresa brillante.', 'Postres', 18.00, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=900&q=80', TRUE, TRUE, CURRENT_DATE),
('Cupcake Bosque Dulce', 'Cupcake de vainilla con crema verde suave y chispas de estrella.', 'Cupcakes', 9.00, 'https://images.unsplash.com/photo-1614707267537-b85aaf00c4b7?auto=format&fit=crop&w=900&q=80', TRUE, FALSE, CURRENT_DATE),
('Café Oráculo', 'Café especial de la casa, perfecto para acompañar un sello de descuento.', 'Bebidas', 8.00, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80', TRUE, FALSE, CURRENT_DATE),
('Galletas Runas Doradas', 'Galletas mantequillosas con canela, vainilla y acabado dorado.', 'Galletas', 10.00, 'https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=900&q=80', TRUE, FALSE, CURRENT_DATE);
