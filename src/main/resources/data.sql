INSERT INTO menu_categories (name, description, is_active, created_at, updated_at)
VALUES
  ('Entradas', 'Platos ligeros para comenzar', true, NOW(), NOW()),
  ('Platos de fondo', 'Opciones principales del menu', true, NOW(), NOW()),
  ('Bebidas', 'Bebidas frias y calientes', true, NOW(), NOW()),
  ('Postres', 'Dulces y opciones para finalizar', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

