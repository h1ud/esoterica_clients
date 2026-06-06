INSERT INTO code (title, description, value_code, discount_code, visibility, is_active, is_used, expiration, created_at) VALUES
('20% OFF Cheesecakes', 'Descuento exclusivo en Cheesecakes premium de la semana.', 20.0, 5.0, 'GLOBAL', true, false, '2026-12-31', '2026-06-06'),
('2x1 Fridays', 'Brownies artesanales al doble los días viernes.', 50.0, 10.0, 'GLOBAL', true, false, '2026-12-31', '2026-06-06'),
('Combo Dulce Místico', 'Café especial + postre con descuento mágico del 15%.', 15.0, 3.0, 'GLOBAL', true, false, '2026-12-31', '2026-06-06');

INSERT INTO clients (name, password_hash, dni, birthday_date) VALUES
('Juan Perez', 'Secreto123', '70805544', '1999-08-15');
