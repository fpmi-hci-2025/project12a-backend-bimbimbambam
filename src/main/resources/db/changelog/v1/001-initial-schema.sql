--liquibase formatted sql

--changeset 9jer:create-users-module-tables
CREATE TABLE roles (
                       role_id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       phone VARCHAR(20),
                       avatar_url VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id INT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

--changeset 9jer:create-catalog-module-tables
CREATE TABLE categories (
                            category_id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            parent_id INT,
                            FOREIGN KEY (parent_id) REFERENCES categories(category_id)
);

CREATE TABLE brands (
                        brand_id SERIAL PRIMARY KEY,
                        name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE products (
                          product_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL,
                          quantity INT NOT NULL DEFAULT 0,
                          average_rating DECIMAL(3, 2) DEFAULT 0.0,
                          popularity BIGINT DEFAULT 0,
                          category_id INT NOT NULL,
                          brand_id INT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(category_id),
                          FOREIGN KEY (brand_id) REFERENCES brands(brand_id)
);

CREATE TABLE product_images (
                                image_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                product_id BIGINT NOT NULL,
                                url VARCHAR(255) NOT NULL,
                                is_primary BOOLEAN DEFAULT FALSE,
                                FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE product_attributes (
                                    attribute_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                    product_id BIGINT NOT NULL,
                                    attribute_name VARCHAR(100) NOT NULL,
                                    attribute_value VARCHAR(255) NOT NULL,
                                    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE favorites (
                           favorite_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           user_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                           FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
                           UNIQUE(user_id, product_id)
);

--changeset 9jer:create-orders-module-tables
CREATE TABLE orders (
                        order_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(50) NOT NULL, -- CREATED, PAID, SHIPPING, COMPLETED, CANCELLED
                        total_price DECIMAL(10, 2) NOT NULL,
                        delivery_address TEXT NOT NULL,
                        contact_phone VARCHAR(20),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE order_items (
                             order_item_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             price_at_purchase DECIMAL(10, 2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(product_id)
);

--changeset 9jer:create-reviews-module-tables
CREATE TABLE reviews (
                         review_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                         user_id BIGINT NOT NULL,
                         product_id BIGINT NOT NULL,
                         rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(user_id),
                         FOREIGN KEY (product_id) REFERENCES products(product_id)
);

--changeset 9jer:create-indexes
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_review_product ON reviews(product_id);