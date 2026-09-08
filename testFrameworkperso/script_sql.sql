DROP DATABASE IF EXISTS testframework;
CREATE DATABASE testframework;
USE testframework;

CREATE TABLE testframework.users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) DEFAULT 'default_user',
    `function` VARCHAR(255) DEFAULT 'default_function'
);

INSERT INTO testframework.users(username, `function`) VALUES
('user1', 'Java Developer'),
('user2', 'Python Developer'),
('user3', 'JavaScript Developer');
