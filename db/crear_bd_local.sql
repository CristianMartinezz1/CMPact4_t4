-- Base de datos y usuario para PRUEBAS EN LOCAL (no usar esta contraseña en el VPS).
-- Ejecutar como root: mysql -u root -p < db/crear_bd_local.sql
CREATE DATABASE IF NOT EXISTS cmpact4_t4 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'cmpact4'@'localhost' IDENTIFIED BY 'cmpact4_local';
GRANT ALL PRIVILEGES ON cmpact4_t4.* TO 'cmpact4'@'localhost';
FLUSH PRIVILEGES;
