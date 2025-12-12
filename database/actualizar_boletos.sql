-- Script para actualizar la tabla boletos con el campo tipo_asiento
-- Ejecutar este script si la columna tipo_asiento no existe en la tabla boletos

USE javatravel_db;

-- Agregar columna tipo_asiento si no existe
ALTER TABLE boletos 
    ADD COLUMN IF NOT EXISTS tipo_asiento VARCHAR(10) NOT NULL DEFAULT 'NORMAL' 
    AFTER asiento;

-- Actualizar registros existentes que no tengan tipo_asiento
UPDATE boletos 
SET tipo_asiento = 'NORMAL' 
WHERE tipo_asiento IS NULL OR tipo_asiento = '';

-- Para buses de dos pisos, actualizar asientos VIP basándose en la numeración
-- Nota: Esto es una aproximación. Los asientos VIP son los primeros según capacidad_piso1
-- Se actualizará manualmente o mediante la aplicación

