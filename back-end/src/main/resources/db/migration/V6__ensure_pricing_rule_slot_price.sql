SET @price_per_slot_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'field_pricing_rules'
      AND column_name = 'price_per_slot'
);

SET @add_price_per_slot_sql = IF(
    @price_per_slot_exists = 0,
    'ALTER TABLE field_pricing_rules ADD COLUMN price_per_slot DECIMAL(19, 2) NULL',
    'SELECT 1'
);

PREPARE add_price_per_slot_stmt FROM @add_price_per_slot_sql;
EXECUTE add_price_per_slot_stmt;
DEALLOCATE PREPARE add_price_per_slot_stmt;

SET @slot_duration_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'field_pricing_rules'
      AND column_name = 'slot_duration'
);

SET @add_slot_duration_sql = IF(
    @slot_duration_exists = 0,
    'ALTER TABLE field_pricing_rules ADD COLUMN slot_duration INT NULL',
    'SELECT 1'
);

PREPARE add_slot_duration_stmt FROM @add_slot_duration_sql;
EXECUTE add_slot_duration_stmt;
DEALLOCATE PREPARE add_slot_duration_stmt;

SET @price_per_hour_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'field_pricing_rules'
      AND column_name = 'price_per_hour'
);

SET @copy_price_sql = IF(
    @price_per_hour_exists > 0,
    'UPDATE field_pricing_rules
     SET price_per_slot = price_per_hour
     WHERE (price_per_slot IS NULL OR price_per_slot = 0)
       AND price_per_hour IS NOT NULL
       AND price_per_hour > 0',
    'SELECT 1'
);

PREPARE copy_price_stmt FROM @copy_price_sql;
EXECUTE copy_price_stmt;
DEALLOCATE PREPARE copy_price_stmt;

UPDATE field_pricing_rules
SET slot_duration = 90
WHERE slot_duration IS NULL OR slot_duration <= 0;
