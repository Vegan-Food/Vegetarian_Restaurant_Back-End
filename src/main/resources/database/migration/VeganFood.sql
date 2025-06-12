DROP DATABASE VeganFood;
CREATE DATABASE VeganFood;
USE VeganFood;
-- User
CREATE TABLE User (  
    user_id INT PRIMARY KEY AUTO_INCREMENT,  
    name VARCHAR(100) UNIQUE,  
    email VARCHAR(100) UNIQUE,  
    password VARCHAR(255),  
    role ENUM('owner', 'staff', 'customer', 'manager'),  
    phone_number VARCHAR(20),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	google_id VARCHAR(100),
    address VARCHAR(100)  
);  

-- SupportTicket
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

-- Discount
CREATE TABLE Discount (  
    discount_id INT PRIMARY KEY AUTO_INCREMENT,  
    discount_code VARCHAR(100),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    status ENUM('Available', 'Unavailable', 'time_expired'),  
    quantity INT  
    
);

-- Order
CREATE TABLE `Order` (  
    order_id INT PRIMARY KEY AUTO_INCREMENT,  
    customer_id INT,  
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,  
    status ENUM('pending', 'shipped', 'delivered', 'cancelled'),  
    payment_method ENUM('VNPAY', 'Cash'),  
    total_amount DECIMAL(10, 2) DEFAULT 0.00,  
    payment_id INT,  
    name VARCHAR(100),  
    phone_number VARCHAR(20),  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    address VARCHAR(100),  
    method ENUM('VNPAY', 'Cash'), 
	discount_id INT,  -- thêm dòng này
    FOREIGN KEY (discount_id) REFERENCES Discount(discount_id)
);  

CREATE TABLE OrderItem (  
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,  
    order_id INT,  
    product_id INT,  
    user_id INT,   -- Thêm dòng này
    quantity INT,  
    price_at_time DECIMAL(10, 2),  
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id),  
    FOREIGN KEY (product_id) REFERENCES Product(product_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)  -- Ràng buộc khóa ngoại mới
);  


-- Product
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




-- Feedback
CREATE TABLE Feedback (  
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,  
    user_id INT,  
    product_id INT,  
    rating TINYINT CHECK (rating BETWEEN 1 AND 5),  
    comment TEXT,  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,  
    FOREIGN KEY (user_id) REFERENCES User(user_id),  
    FOREIGN KEY (product_id) REFERENCES Product(product_id)  
);  



CREATE TABLE Cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE CartItem (
    cart_item_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (cart_id) REFERENCES Cart(cart_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);


CREATE TABLE FAQ (
    faq_id INT PRIMARY KEY AUTO_INCREMENT,
    question TEXT NOT NULL,
    answer TEXT NOT NULL
); 