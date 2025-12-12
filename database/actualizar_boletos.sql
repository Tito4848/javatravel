-- Script para actualizar la tabla boletos con el campo tipo_asiento
-- Ejecutar este script si la columna tipo_asiento no existe en la tabla boletos
-- NOTA: MySQL no soporta IF NOT EXISTS en ALTER TABLE, por lo que este script
-- puede fallar si la columna ya existe. En ese caso, ignorar el error.

USE javatravel_db;

-- Verificar si la columna existe antes de agregarla (solo para referencia)
-- Si la columna ya existe, este comando fallará con error 1060
-- Eso es normal y puede ignorarse

-- Agregar columna tipo_asiento
ALTER TABLE boletos 
    ADD COLUMN tipo_asiento VARCHAR(10) NOT NULL DEFAULT 'NORMAL' 
    AFTER asiento;

-- Actualizar registros existentes que no tengan tipo_asiento
UPDATE boletos 
SET tipo_asiento = 'NORMAL' 
WHERE tipo_asiento IS NULL OR tipo_asiento = '';

-- Para buses de dos pisos, actualizar asientos VIP basándose en la numeración
-- Nota: Esto es una aproximación. Los asientos VIP son los primeros según capacidad_piso1
-- Se actualizará manualmente o mediante la aplicación

