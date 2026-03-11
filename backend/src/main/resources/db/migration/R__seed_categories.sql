-- R__seed_categories.sql
-- Repeatable migration: re-run whenever checksum changes
-- Uses ON CONFLICT to stay idempotent

INSERT INTO categories (name, slug) VALUES
    ('Electronics',       'electronics'),
    ('Computers',         'computers'),
    ('Phones & Tablets',  'phones-tablets'),
    ('Accessories',       'accessories'),
    ('Books',             'books'),
    ('Home & Kitchen',    'home-kitchen'),
    ('Apparel',           'apparel'),
    ('Toys & Games',      'toys-games')
ON CONFLICT (slug) DO UPDATE SET
    name = EXCLUDED.name;