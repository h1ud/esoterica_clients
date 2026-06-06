ALTER TABLE clients ADD COLUMN role VARCHAR(20) DEFAULT 'client';

-- Insert admin user
INSERT INTO clients (name, password_hash, dni, birthday_date, role) VALUES
('Administrador Esotérica', 'Admin123', '99999999', '1990-01-01', 'admin');
