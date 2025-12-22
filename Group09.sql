-- Database: group09_greengrocer
-- User: myuser / 1234
-- Re-create schema
DROP DATABASE IF EXISTS group09_greengrocer;
CREATE DATABASE group09_greengrocer;
USE group09_greengrocer;

-- 1. UserInfo Table
CREATE TABLE UserInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- CUSTOMER, CARRIER, OWNER
    full_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ProductInfo Table
CREATE TABLE ProductInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- VEGETABLE, FRUIT
    price_per_kg DOUBLE NOT NULL,
    stock_kg DOUBLE NOT NULL DEFAULT 0,
    threshold_kg DOUBLE NOT NULL DEFAULT 5,
    image_path VARCHAR(255), -- Storing path or base64 blob in app logic, simple string here for path ref
    is_active BOOLEAN DEFAULT TRUE
);

-- 3. OrderInfo Table
CREATE TABLE OrderInfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    carrier_id INT, -- Nullable until assigned
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    requested_delivery_time TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'CREATED', -- CREATED, PLACED, ASSIGNED, DELIVERED, CANCELED
    total_amount DOUBLE NOT NULL DEFAULT 0,
    customer_address VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES UserInfo(id),
    FOREIGN KEY (carrier_id) REFERENCES UserInfo(id)
);

-- 4. OrderItem Table
CREATE TABLE OrderItem (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    amount_kg DOUBLE NOT NULL,
    unit_price DOUBLE NOT NULL, -- Price at the time of order
    line_total DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES OrderInfo(id),
    FOREIGN KEY (product_id) REFERENCES ProductInfo(id)
);

-- 5. CarrierRating Table
CREATE TABLE CarrierRating (
    id INT AUTO_INCREMENT PRIMARY KEY,
    carrier_id INT NOT NULL,
    customer_id INT NOT NULL,
    order_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    FOREIGN KEY (carrier_id) REFERENCES UserInfo(id),
    FOREIGN KEY (customer_id) REFERENCES UserInfo(id),
    FOREIGN KEY (order_id) REFERENCES OrderInfo(id)
);

-- 6. Message Table (Customer <-> Owner)
CREATE TABLE Message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL, -- Could be 0 for 'System/Owner' generic
    content TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES UserInfo(id)
    -- receiver_id might strictly link to UserInfo or handle loosely
);

-- Initial Data
-- Common password '1234' hash: 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
INSERT INTO UserInfo (username, password_hash, role, full_name, phone, email, address) VALUES 
('cust', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CUSTOMER', 'John Customer', '555-0001', 'cust@mail.com', '123 Customer St'),
('carr', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'CARRIER', 'Speedy Carrier', '555-0002', 'carr@mail.com', 'Carrier HQ'),
('own',  '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'OWNER',    'Big Boss',      '555-0003', 'boss@mail.com', 'Office');

-- Initial Products (12 Vegetables + 12 Fruits)
INSERT INTO ProductInfo (name, type, price_per_kg, stock_kg, threshold_kg) VALUES
('Tomato', 'VEGETABLE', 2.5, 50, 5),
('Potato', 'VEGETABLE', 1.0, 100, 10),
('Onion', 'VEGETABLE', 1.2, 80, 5),
('Cucumber', 'VEGETABLE', 1.5, 40, 5),
('Carrot', 'VEGETABLE', 1.8, 45, 5),
('Pepper', 'VEGETABLE', 3.0, 30, 5),
('Lettuce', 'VEGETABLE', 2.0, 20, 2),
('Broccoli', 'VEGETABLE', 3.5, 25, 5),
('Spinach', 'VEGETABLE', 2.2, 20, 3),
('Garlic', 'VEGETABLE', 5.0, 15, 2),
('Pumpkin', 'VEGETABLE', 1.5, 50, 5),
('Cabbage', 'VEGETABLE', 1.8, 30, 5),

('Apple', 'FRUIT', 3.0, 60, 5),
('Banana', 'FRUIT', 4.0, 50, 5),
('Orange', 'FRUIT', 3.5, 55, 5),
('Grape', 'FRUIT', 5.0, 30, 3),
('Strawberry', 'FRUIT', 6.0, 20, 2),
('Melon', 'FRUIT', 2.5, 40, 5),
('Watermelon', 'FRUIT', 1.5, 80, 10),
('Peach', 'FRUIT', 4.5, 35, 5),
('Pear', 'FRUIT', 3.8, 40, 5),
('Cherry', 'FRUIT', 7.0, 15, 2),
('Kiwi', 'FRUIT', 5.5, 20, 3),
('Pineapple', 'FRUIT', 6.5, 25, 3);
