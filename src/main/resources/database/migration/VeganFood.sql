CREATE DATABASE vegan;
USE vegan;
-- 1. User
CREATE TABLE User (  
    user_id INT PRIMARY KEY AUTO_INCREMENT,  
    name VARCHAR(100) UNIQUE,  
    email VARCHAR(100) UNIQUE,  
    password VARCHAR(255),  
    role ENUM('owner', 'staff', 'customer', 'manager'),  
    phone_number VARCHAR(20),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    google_token TEXT,
    address VARCHAR(100)
);  


-- insert bảng user
INSERT INTO User (name, email, password, role, phone_number, google_token, address)
VALUES 
('quanadmin', 'quanadmin@example.com', '$2a$10$YJsjFau26AkczcHQY.JnW.Jcys1kV3IoT2OF3nLCaS96HzAZJmXjq', 'owner', '0975988777', NULL, '107 tran dai nghia');

UPDATE User 
SET password = '$2a$10$60WUCQ3m7a2IEPCVlzZS6uGN9qpKvIaNw.KDVKJsJu.AZEk8bHKSa'
WHERE email = 'levanc@example.com';

select * from user;
SELECT email, password FROM User WHERE email = 'levanc@example.com';

SELECT * FROM User;

-- 2. Product
CREATE TABLE Product (  
    product_id INT PRIMARY KEY AUTO_INCREMENT,  
    name VARCHAR(100),  
    description TEXT,  
    price DECIMAL(10, 2),  
    stock_quantity INT,  
    image_url VARCHAR(255),  
    category VARCHAR(100),  
    total_order INT  
);
INSERT INTO Product (name, description, price, stock_quantity, image_url, category, total_order)
VALUES 
('Cơm chay thập cẩm', 'Cơm trắng ăn kèm rau củ, đậu hủ chiên, nấm xào và chả chay', 35000, 100, 'https://example.com/com-chay.jpg', 'Main Course', 0),

('Bún riêu chay', 'Bún ăn kèm nước lèo đậm đà từ nấm, đậu hủ non và cà chua', 30000, 80, 'https://example.com/bun-rieu-chay.jpg', 'Noodle Soup', 0),

('Gỏi cuốn chay', 'Cuốn rau củ, bún và đậu hủ kèm nước chấm tương đậu phộng', 25000, 120, 'https://example.com/goi-cuon.jpg', 'Appetizer', 0),

('Đậu hũ chiên sả ớt', 'Đậu hũ chiên giòn, tẩm ướp sả và ớt cay nhẹ', 20000, 90, 'https://example.com/dau-hu-sa-ot.jpg', 'Side Dish', 0),

('Lẩu nấm chay', 'Lẩu với nhiều loại nấm như nấm kim châm, nấm bào ngư, nấm đùi gà, rau củ tươi', 120000, 40, 'https://example.com/lau-nam.jpg', 'Hot Pot', 0),

('Súp bí đỏ chay', 'Súp mịn từ bí đỏ, kem thực vật, rất phù hợp cho món khai vị nhẹ nhàng', 18000, 70, 'https://example.com/sup-bi-do.jpg', 'Appetizer', 0),

('Chè đậu xanh', 'Món tráng miệng thanh mát với đậu xanh, nước cốt dừa', 15000, 60, 'https://example.com/che-dau-xanh.jpg', 'Dessert', 0);


select * from product;
-- 3. Discount
CREATE TABLE Discount (  
    discount_id INT PRIMARY KEY AUTO_INCREMENT,  
    discount_code VARCHAR(100),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    status ENUM('Available', 'Unavailable', 'time_expired'),  
    quantity INT,
    percentage INT NOT NULL CHECK (percentage >= 0 AND percentage <= 100)
);

-- 4. Order
CREATE TABLE `Order` (  
    order_id INT PRIMARY KEY AUTO_INCREMENT,  
    customer_id INT,  
    status ENUM('pending', 'shipped', 'delivered', 'cancelled'),  
    payment_method ENUM('VNPAY', 'Cash'),  
    total_amount DECIMAL(10, 2) DEFAULT 0.00,  
    phone_number VARCHAR(20),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    address VARCHAR(100),  
    discount_id INT,
    FOREIGN KEY (customer_id) REFERENCES User(user_id),
    FOREIGN KEY (discount_id) REFERENCES Discount(discount_id)
);

-- 5. OrderItem
CREATE TABLE OrderItem (  
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,  
    order_id INT,  
    product_id INT,  
    user_id INT,  
    quantity INT,  
    price_at_time DECIMAL(10, 2),  
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id),  
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);
select * from CartItem;
-- 6. Cart
CREATE TABLE Cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- 7. CartItem
CREATE TABLE CartItem (
    cart_item_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (cart_id) REFERENCES Cart(cart_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

-- 8. Feedback
CREATE TABLE Feedback (  
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,  
    user_id INT,  
    product_id INT,  
    comment TEXT,  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    FOREIGN KEY (user_id) REFERENCES User(user_id),  
    FOREIGN KEY (product_id) REFERENCES Product(product_id)  
);
INSERT INTO Feedback (user_id, product_id, comment)
VALUES 
(1, 1, 'Món cơm rất ngon, đậm đà và hợp khẩu vị chay.'),
(1, 2, 'Bún riêu chay thơm ngon, nước dùng vị chay thanh mát.'),
(1, 3, 'Gỏi cuốn tươi, ăn kèm nước chấm rất vừa miệng.'),
(1, 4, 'Đậu hũ chiên giòn, sả ớt cay nhẹ rất kích thích vị giác.'),
(1, 5, 'Lẩu nấm nhiều loại nấm tươi, ăn cực kỳ thích.'),
(1, 6, 'Súp bí đỏ mịn và ấm bụng, thích hợp cho khai vị.'),
(1, 7, 'Chè đậu xanh ngọt vừa, ăn mát và nhẹ bụng.');
select * from feedback;
-- 9. SupportTicket
CREATE TABLE SupportTicket (  
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,  
    user_id INT,  
    subject VARCHAR(255),  
    description TEXT,  
    status ENUM('open', 'closed', 'pending'),  
    assigned_to INT,  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    FOREIGN KEY (user_id) REFERENCES User(user_id),  
    FOREIGN KEY (assigned_to) REFERENCES User(user_id)  
);

-- 10. FAQ
CREATE TABLE FAQ (
    faq_id INT PRIMARY KEY AUTO_INCREMENT,
    question TEXT NOT NULL,
    answer TEXT NOT NULL
);
