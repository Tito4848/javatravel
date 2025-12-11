-- Script de datos de prueba para JavaTravel
-- Ejecutar después de crear las tablas

USE javatravel_db;

-- Limpiar datos existentes (opcional, comentar si no se desea)
-- DELETE FROM boletos;
-- DELETE FROM viajes;
-- DELETE FROM pasajeros;
-- DELETE FROM rutas;
-- DELETE FROM buses;

-- Insertar Buses
INSERT INTO buses (placa, marca, tipo_bus, capacidad) VALUES
('ABC-123', 'Mercedes Benz', 'ESTANDAR', 40),
('XYZ-456', 'Volvo', 'ESTANDAR', 45),
('DEF-789', 'Scania', 'DOS_PISOS', 60),
('GHI-012', 'Mercedes Benz', 'DOS_PISOS', 55),
('JKL-345', 'Volvo', 'ESTANDAR', 42),
('MNO-678', 'Scania', 'DOS_PISOS', 58),
('PQR-901', 'Mercedes Benz', 'ESTANDAR', 38);

-- Insertar Rutas
INSERT INTO rutas (origen, destino, precio_base, duracion_horas) VALUES
('Lima', 'Arequipa', 80.00, 12),
('Lima', 'Trujillo', 50.00, 8),
('Lima', 'Cusco', 100.00, 20),
('Lima', 'Chiclayo', 60.00, 10),
('Lima', 'Piura', 70.00, 12),
('Arequipa', 'Lima', 80.00, 12),
('Arequipa', 'Cusco', 70.00, 10),
('Trujillo', 'Lima', 50.00, 8),
('Trujillo', 'Chiclayo', 30.00, 4),
('Cusco', 'Lima', 100.00, 20),
('Cusco', 'Arequipa', 70.00, 10),
('Chiclayo', 'Lima', 60.00, 10);

-- Insertar Viajes (para hoy, mañana y pasado mañana)
INSERT INTO viajes (id_bus, id_ruta, fecha_salida, hora_salida) VALUES
-- Viajes para hoy
(1, 1, CURDATE(), '08:00:00'),
(2, 2, CURDATE(), '10:00:00'),
(3, 1, CURDATE(), '14:00:00'),
(4, 3, CURDATE(), '20:00:00'),
(5, 4, CURDATE(), '06:00:00'),
(6, 1, CURDATE(), '16:00:00'),
-- Viajes para mañana
(1, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:00:00'),
(2, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '20:00:00'),
(3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00'),
(4, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00'),
(5, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '06:00:00'),
(6, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '16:00:00'),
-- Viajes para pasado mañana
(1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '08:00:00'),
(2, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00:00'),
(3, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:00:00'),
(4, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '06:00:00');

-- Insertar Pasajeros de ejemplo
INSERT INTO pasajeros (dni, nombre, edad) VALUES
('12345678', 'Juan Pérez García', 35),
('87654321', 'María García López', 28),
('11223344', 'Carlos López Martínez', 45),
('44332211', 'Ana Martínez Sánchez', 15),
('55667788', 'Pedro Sánchez Fernández', 70),
('99887766', 'Laura Fernández Torres', 25),
('22334455', 'Roberto Torres Vargas', 32),
('33445566', 'Carmen Vargas Rojas', 55),
('44556677', 'Miguel Rojas Díaz', 12),
('66778899', 'Sofía Díaz Morales', 68),
('77889900', 'Diego Morales Castro', 40),
('88990011', 'Patricia Castro Ruiz', 22);

-- Insertar Boletos de ejemplo (algunos asientos ocupados para mostrar en el mapa)
INSERT INTO boletos (id_viaje, id_pasajero, asiento, precio_final, fecha_compra) VALUES
-- Viaje 1 (Lima-Arequipa, hoy 08:00) - Algunos asientos ocupados
(1, 1, 'A1', 80.00, NOW()),
(1, 2, 'B1', 80.00, NOW()),
(1, 3, 'C1', 80.00, NOW()),
(1, 4, 'D1', 68.00, NOW()), -- Menor de edad
(1, 5, 'A2', 64.00, NOW()), -- Adulto mayor
-- Viaje 2 (Lima-Trujillo, hoy 10:00) - Algunos asientos ocupados
(2, 6, 'A1', 50.00, NOW()),
(2, 7, 'B1', 50.00, NOW()),
(2, 8, 'C1', 50.00, NOW()),
(2, 9, 'D1', 42.50, NOW()), -- Menor de edad
(2, 10, 'A2', 40.00, NOW()), -- Adulto mayor
-- Viaje 3 (Lima-Arequipa, hoy 14:00, Bus DOS_PISOS) - Algunos VIP ocupados
(3, 11, 'A1', 96.00, NOW()), -- VIP normal
(3, 12, 'B1', 96.00, NOW()), -- VIP normal
(3, 1, 'A2', 81.60, NOW()), -- VIP menor de edad
(3, 5, 'B2', 76.80, NOW()); -- VIP adulto mayor

