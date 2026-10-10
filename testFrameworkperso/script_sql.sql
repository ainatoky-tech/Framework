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

CREATE TABLE testframework.extension (
    idextension INT AUTO_INCREMENT PRIMARY KEY,
    iduser INT DEFAULT 1,
    poste VARCHAR(255)
);

INSERT INTO testframework.extension(iduser,poste) VALUES
(2, 'chief'),
(1, 'mentor'),
(3, 'older');