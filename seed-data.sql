-- =============================================
-- SEED DATA: 5 Categories + 10 Products each
-- Run this on product_db
-- =============================================

USE product_db;

-- =============================================
-- CATEGORIES
-- =============================================
INSERT INTO category (id, name, description) VALUES
(1, 'Electronics',    'Gadgets, devices and electronic accessories'),
(2, 'Clothing',       'Men and women fashion and apparel'),
(3, 'Home & Kitchen', 'Furniture, appliances and kitchen essentials'),
(4, 'Sports',         'Sports equipment and outdoor gear'),
(5, 'Books',          'Bestsellers, textbooks and novels');

-- =============================================
-- PRODUCTS — Electronics (category_id = 1)
-- =============================================
INSERT INTO product (name, description, price, image_url, brand, active, category_id, created_at) VALUES
('iPhone 15 Pro',         'Apple iPhone 15 Pro 256GB, Titanium finish, A17 Pro chip',                          999.99,  'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600', 'Apple',   true, 1, NOW()),
('Samsung Galaxy S24',    'Samsung Galaxy S24 Ultra 512GB, AI-powered camera system',                          849.99,  'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600', 'Samsung', true, 1, NOW()),
('Sony WH-1000XM5',       'Industry-leading noise cancelling wireless headphones',                              349.99,  'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', 'Sony',    true, 1, NOW()),
('MacBook Air M3',        'Apple MacBook Air 13-inch with M3 chip, 8GB RAM, 256GB SSD',                       1099.99, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600', 'Apple',   true, 1, NOW()),
('iPad Pro 12.9',         'Apple iPad Pro 12.9-inch with M2 chip and Liquid Retina XDR display',               1199.99, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600', 'Apple',   true, 1, NOW()),
('Dell XPS 15',           'Dell XPS 15 laptop, Intel Core i7, 16GB RAM, 512GB SSD, OLED display',              1299.99, 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=600', 'Dell',    true, 1, NOW()),
('Canon EOS R50',         'Canon EOS R50 mirrorless camera, 24.2MP, 4K video recording',                       679.99,  'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600', 'Canon',   true, 1, NOW()),
('Apple Watch Series 9',  'Apple Watch Series 9 GPS 45mm, Always-On Retina display',                           399.99,  'https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600', 'Apple',   true, 1, NOW()),
('LG 4K OLED TV 55"',     'LG 55-inch 4K OLED Smart TV with webOS and Dolby Vision',                          1499.99, 'https://images.unsplash.com/photo-1593359677879-a4bb92f829e1?w=600', 'LG',      true, 1, NOW()),
('Logitech MX Master 3',  'Advanced wireless mouse with ultra-fast scrolling and ergonomic design',              99.99,  'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600', 'Logitech',true, 1, NOW());

-- =============================================
-- PRODUCTS — Clothing (category_id = 2)
-- =============================================
INSERT INTO product (name, description, price, image_url, brand, active, category_id, created_at) VALUES
('Classic White T-Shirt',     'Premium 100% cotton unisex white t-shirt, available in all sizes',           19.99,  'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600', 'H&M',       true, 2, NOW()),
('Slim Fit Jeans',            'Men slim fit dark blue denim jeans, stretch fabric for comfort',              49.99,  'https://images.unsplash.com/photo-1542272604-787c3835535d?w=600', 'Levi\'s',   true, 2, NOW()),
('Floral Summer Dress',       'Women lightweight floral print midi dress, perfect for summer',               39.99,  'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600', 'Zara',      true, 2, NOW()),
('Hooded Sweatshirt',         'Unisex pullover hoodie, fleece lined, kangaroo pocket',                       44.99,  'https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=600', 'Nike',      true, 2, NOW()),
('Leather Jacket',            'Men genuine leather biker jacket with zip pockets',                          129.99,  'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600', 'Zara',      true, 2, NOW()),
('Running Sneakers',          'Lightweight breathable running shoes with cushioned sole',                     79.99,  'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 'Adidas',    true, 2, NOW()),
('Formal Blazer',             'Men slim fit formal blazer, perfect for office and events',                    89.99,  'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=600', 'Marks & Spencer', true, 2, NOW()),
('Yoga Pants',                'Women high-waist yoga leggings with moisture-wicking fabric',                  34.99,  'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=600', 'Lululemon', true, 2, NOW()),
('Wool Overcoat',             'Women long wool blend overcoat, double-breasted, winter essential',           159.99,  'https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=600', 'Mango',     true, 2, NOW()),
('Polo Shirt',                'Men classic fit cotton polo shirt, available in multiple colors',              29.99,  'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600', 'Ralph Lauren', true, 2, NOW());

-- =============================================
-- PRODUCTS — Home & Kitchen (category_id = 3)
-- =============================================
INSERT INTO product (name, description, price, image_url, brand, active, category_id, created_at) VALUES
('Nespresso Coffee Machine',  'Nespresso Vertuo Next coffee and espresso machine with milk frother',          149.99,  'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600', 'Nespresso',  true, 3, NOW()),
('KitchenAid Stand Mixer',    'KitchenAid Artisan 5-quart stand mixer with 10 speeds',                       379.99,  'https://images.unsplash.com/photo-1594385208974-2e75f8d7bb48?w=600', 'KitchenAid', true, 3, NOW()),
('Instant Pot Duo 7-in-1',    'Electric pressure cooker, slow cooker, rice cooker, steamer and more',         89.99,  'https://images.unsplash.com/photo-1585515320310-259814833e62?w=600', 'Instant Pot',true, 3, NOW()),
('Dyson V15 Vacuum',          'Dyson V15 Detect cordless vacuum with laser dust detection',                   649.99,  'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600', 'Dyson',      true, 3, NOW()),
('Memory Foam Pillow',        'Ergonomic memory foam pillow with cooling gel layer for better sleep',          49.99,  'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600', 'Tempur',     true, 3, NOW()),
('Air Purifier HEPA',         'True HEPA air purifier covers up to 500 sq ft, removes 99.97% particles',     199.99,  'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600', 'Levoit',     true, 3, NOW()),
('Non-Stick Cookware Set',    '10-piece non-stick cookware set with glass lids, dishwasher safe',             119.99,  'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600', 'Tefal',      true, 3, NOW()),
('Smart LED Desk Lamp',       'Touch control LED desk lamp with USB charging port and adjustable brightness',  39.99,  'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=600', 'Philips',    true, 3, NOW()),
('Wooden Cutting Board',      'Large acacia wood cutting board with juice groove, 18x12 inch',                34.99,  'https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?w=600', 'OXO',        true, 3, NOW()),
('French Press Coffee Maker', 'Stainless steel French press 34oz, double wall insulated',                     29.99,  'https://images.unsplash.com/photo-1510591509098-f4fdc6d0ff04?w=600', 'Bodum',      true, 3, NOW());

-- =============================================
-- PRODUCTS — Sports (category_id = 4)
-- =============================================
INSERT INTO product (name, description, price, image_url, brand, active, category_id, created_at) VALUES
('Yoga Mat',                  'Non-slip 6mm thick yoga mat with carrying strap, eco-friendly TPE material',   29.99,  'https://images.unsplash.com/photo-1601925228008-f5e4c5e5e5e5?w=600', 'Manduka',   true, 4, NOW()),
('Adjustable Dumbbells',      'Adjustable dumbbell set 5-52.5 lbs, replaces 15 sets of weights',             299.99,  'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600', 'Bowflex',   true, 4, NOW()),
('Mountain Bike Helmet',      'Adult mountain bike helmet with MIPS protection, 20 vents',                    89.99,  'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?w=600', 'Giro',      true, 4, NOW()),
('Running Water Bottle',      'Insulated stainless steel water bottle 32oz, keeps cold 24hrs',                24.99,  'https://images.unsplash.com/photo-1523362628745-0c100150b504?w=600', 'Hydro Flask',true, 4, NOW()),
('Resistance Bands Set',      'Set of 5 resistance bands with different tension levels, includes bag',         19.99,  'https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=600', 'TheraBand', true, 4, NOW()),
('Tennis Racket',             'Professional tennis racket, graphite frame, pre-strung, for all levels',       79.99,  'https://images.unsplash.com/photo-1617083934555-ac7b4d0c8be2?w=600', 'Wilson',    true, 4, NOW()),
('Football',                  'Official size 5 football, FIFA quality pro, butyl bladder',                    34.99,  'https://images.unsplash.com/photo-1575361204480-aadea25e6e68?w=600', 'Adidas',    true, 4, NOW()),
('Jump Rope',                 'Speed jump rope with ball bearings, adjustable cable, foam handles',            14.99,  'https://images.unsplash.com/photo-1434682881908-b43d0467b798?w=600', 'Crossrope', true, 4, NOW()),
('Gym Gloves',                'Weight lifting gloves with wrist support, anti-slip palm padding',             19.99,  'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600', 'Harbinger', true, 4, NOW()),
('Foam Roller',               'High-density foam roller for muscle recovery and deep tissue massage',          24.99,  'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=600', 'TriggerPoint', true, 4, NOW());

-- =============================================
-- PRODUCTS — Books (category_id = 5)
-- =============================================
INSERT INTO product (name, description, price, image_url, brand, active, category_id, created_at) VALUES
('Clean Code',                'A Handbook of Agile Software Craftsmanship by Robert C. Martin',               34.99,  'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600', 'Prentice Hall',  true, 5, NOW()),
('The Pragmatic Programmer',  'Your Journey to Mastery by David Thomas and Andrew Hunt',                       39.99,  'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', 'Addison-Wesley', true, 5, NOW()),
('Atomic Habits',             'An Easy and Proven Way to Build Good Habits by James Clear',                    16.99,  'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', 'Avery',          true, 5, NOW()),
('System Design Interview',   'An Insider\'s Guide Volume 1 by Alex Xu, essential for tech interviews',        35.99,  'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600', 'Independently',  true, 5, NOW()),
('The Alchemist',             'A magical story about following your dreams by Paulo Coelho',                   12.99,  'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600', 'HarperOne',      true, 5, NOW()),
('Deep Work',                 'Rules for Focused Success in a Distracted World by Cal Newport',                17.99,  'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600', 'Grand Central',  true, 5, NOW()),
('Design Patterns',           'Elements of Reusable Object-Oriented Software by Gang of Four',                 44.99,  'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600', 'Addison-Wesley', true, 5, NOW()),
('The Psychology of Money',   'Timeless lessons on wealth, greed and happiness by Morgan Housel',              15.99,  'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=600', 'Harriman House', true, 5, NOW()),
('Dune',                      'Epic science fiction novel by Frank Herbert, basis of the 2021 film',           14.99,  'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600', 'Ace Books',      true, 5, NOW()),
('Harry Potter Box Set',      'Complete Harry Potter 7-book collection by J.K. Rowling, hardcover',            89.99,  'https://images.unsplash.com/photo-1551269901-5c5e68ef8e6e?w=600', 'Scholastic',     true, 5, NOW());

SELECT CONCAT('Categories: ', COUNT(*)) as summary FROM category
UNION ALL
SELECT CONCAT('Products: ',   COUNT(*)) FROM product;
