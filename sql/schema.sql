CREATE DATABASE agenda_db;
USE agenda_db;

CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente VARCHAR(35) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    servicio VARCHAR(35) NOT NULL,
    duracion_minutos INT NOT NULL,
    estado ENUM('pendiente', 'confirmada', 'cancelada') NOT NULL DEFAULT 'pendiente'
);