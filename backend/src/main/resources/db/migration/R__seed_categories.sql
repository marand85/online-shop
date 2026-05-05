-- R__seed_categories.sql
-- Repeatable migration: re-run whenever checksum changes with explicit IDs for deterministic testing
-- Uses ON CONFLICT to stay idempotent

INSERT INTO categories (id, name, slug) VALUES
    (1, 'Electronics',       'electronics'),
    (2, 'Computers',         'computers'),
    (3, 'Phones & Tablets',  'phones-tablets'),
    (4, 'Accessories',       'accessories'),
    (5, 'Books',             'books'),
    (6, 'Home & Kitchen',    'home-kitchen'),
    (7, 'Apparel',           'apparel'),
    (8, 'Toys & Games',      'toys-games')
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name;