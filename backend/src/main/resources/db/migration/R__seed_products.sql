-- R__seed_products.sql
-- Repeatable migration - adds test products

INSERT INTO products (
    category_id, sku, gtin14, name, description, price_cents, currency,
    stock_qty, image_url, active, created_at
)
VALUES
    (1, 'LAPTOP-X1',     '01234567890123', 'Lenovo ThinkPad X1 Carbon', 'High-end business ultrabook',  649900, 'PLN', 45, 'https://picsum.photos/id/20/600/400',  true, now()),
    (1, 'PHONE-15PRO',   '12345678901234', 'iPhone 15 Pro 256GB',       'Latest Apple flagship',       529900, 'PLN', 23, 'https://picsum.photos/id/30/600/400',  true, now()),
    (2, 'MONITOR-27QHD', '23456789012345', 'Dell UltraSharp 27"',       'Professional 1440p monitor',   189900, 'PLN', 12, 'https://picsum.photos/id/45/600/400',  true, now()),
    (3, 'HEADPHONE-XM5', '34567890123456', 'Sony WH-1000XM5',           'Best noise cancelling headphones',  149900, 'PLN', 67, 'https://picsum.photos/id/60/600/400',  true, now()),
    (4, 'BACKPACK-TECH', '45678901234567', 'Tech Backpack 25L',         'Waterproof laptop backpack',    89900, 'PLN', 34, 'https://picsum.photos/id/80/600/400',  true, now()),
    (5, 'BOOK-CLEANCODE','56789012345678', 'Clean Code - Robert Martin','Classic software engineering book',  8900, 'PLN', 98, 'https://picsum.photos/id/101/600/400', true, now()),
    (6, 'BLENDER-800W',  '67890123456789', 'Professional Blender 800W',  'High power kitchen blender',    29900, 'PLN', 15, 'https://picsum.photos/id/160/600/400', true, now()),
    (7, 'HOODIE-BASIC',  '78901234567890', 'Basic Cotton Hoodie',        'Comfortable everyday hoodie',   12900, 'PLN', 120, 'https://picsum.photos/id/201/600/400', true, now())
ON CONFLICT (sku) DO UPDATE SET
    name = EXCLUDED.name,
    price_cents = EXCLUDED.price_cents,
    stock_qty = EXCLUDED.stock_qty;