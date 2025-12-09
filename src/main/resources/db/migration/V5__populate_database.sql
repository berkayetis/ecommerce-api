INSERT INTO categories (name)
VALUES ('Fruits'),
       ('Vegetables'),
       ('Dairy'),
       ('Bakery'),
       ('Meat'),
       ('Beverages');

INSERT INTO products (name, price, description, category_id)
VALUES ('Bananas', 1.29, 'Fresh yellow bananas, sold per pound', 1),
       ('Gala Apples', 2.49, 'Crisp and sweet red apples, 1 lb bag', 1),
       ('Carrots', 1.99, 'Fresh organic carrots, 1 lb', 2),
       ('Broccoli', 2.29, 'Fresh green broccoli crowns', 2),
       ('Whole Milk', 3.59, '1 gallon of pasteurized whole cow\'s milk', 3),
       ('Cheddar Cheese', 4.99, 'Sharp cheddar cheese block, 200g', 3),
       ('White Bread', 2.79, 'Soft bakery-style sandwich bread, 24 slices', 4),
       ('Croissant Pack', 4.49, 'Six freshly baked butter croissants', 4),
       ('Ground Beef', 7.99, '80/20 ground beef, 1 lb pack', 5),
       ('Orange Juice', 3.89, '100% pure squeezed orange juice, no sugar added, 1L', 6);
