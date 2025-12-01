--liquibase formatted sql

--changeset 9jer:insert-roles
INSERT INTO roles (name) VALUES ('ROLE_GUEST'), ('ROLE_USER'), ('ROLE_ADMIN');

--changeset 9jer:insert-users
-- Password is 'test-password' (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, phone, created_at)
VALUES
    ('admin', 'admin@techstore.com', '$2a$10$VsRKFaWgMJkEAmEzk0taN.T8WQ8sot6UqDdXd3NAXILNVTfT3zpfK', 'Admin', 'Super', '+1000000000', CURRENT_TIMESTAMP),
    ('user', 'user@techstore.com', '$2a$10$VsRKFaWgMJkEAmEzk0taN.T8WQ8sot6UqDdXd3NAXILNVTfT3zpfK', 'John', 'Doe', '+1234567890', CURRENT_TIMESTAMP);

INSERT INTO users_roles (user_id, role_id) VALUES
                                               (1, 3), -- Admin -> ROLE_ADMIN
                                               (2, 2); -- User -> ROLE_USER

--changeset 9jer:insert-catalog-data
INSERT INTO brands (name) VALUES ('Apple'), ('Samsung'), ('Sony'), ('Dell'), ('Asus'), ('Logitech');

INSERT INTO categories (name, parent_id) VALUES
                                             ('Ноутбуки', NULL),
                                             ('Смартфоны', NULL),
                                             ('Наушники', NULL),
                                             ('Планшеты', NULL);

INSERT INTO products (title, description, price, quantity, average_rating, category_id, brand_id, created_at)
VALUES ('Apple MacBook Pro 16', 'Мощный ноутбук для профессионалов с процессором M3 Pro', 249990.00, 10, 4.9, 1, 1, CURRENT_TIMESTAMP);

INSERT INTO products (title, description, price, quantity, average_rating, category_id, brand_id, created_at)
VALUES ('Samsung Galaxy S24 Ultra', 'Флагманский смартфон с камерой 200 Мп и S Pen', 129990.00, 15, 4.7, 2, 2, CURRENT_TIMESTAMP);

INSERT INTO products (title, description, price, quantity, average_rating, category_id, brand_id, created_at)
VALUES ('Sony WH-1000XM5', 'Беспроводные наушники с лучшим шумоподавлением', 34990.00, 50, 4.8, 3, 3, CURRENT_TIMESTAMP);

INSERT INTO product_attributes (product_id, attribute_name, attribute_value) VALUES
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Apple MacBook%'), 'Процессор', 'Apple M3 Pro'),
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Apple MacBook%'), 'RAM', '36 ГБ'),
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Apple MacBook%'), 'SSD', '1 ТБ');

INSERT INTO product_attributes (product_id, attribute_name, attribute_value) VALUES
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Samsung%'), 'Процессор', 'Snapdragon 8 Gen 3'),
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Samsung%'), 'Камера', '200 Мп'),
                                                                                 ((SELECT product_id FROM products WHERE title LIKE 'Samsung%'), 'Память', '12/512 ГБ');