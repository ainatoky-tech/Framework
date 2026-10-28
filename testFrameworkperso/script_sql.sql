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

CREATE TABLE testframework.files(
    id INT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255),
    extension VARCHAR(22),
    content LONGBLOB,          -- ← les bytes
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE testframework.administrator(
    id_administrator INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(55),
    id_role INT ,
    FOREIGN KEY (id_role) REFERENCES testframework.extension(id)
);