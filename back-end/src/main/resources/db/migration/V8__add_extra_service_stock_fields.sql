SET @track_stock_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND column_name = 'track_stock'
);

SET @track_stock_sql = IF(
    @track_stock_exists = 0,
    'ALTER TABLE extra_services ADD COLUMN track_stock BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE track_stock_stmt FROM @track_stock_sql;
EXECUTE track_stock_stmt;
DEALLOCATE PREPARE track_stock_stmt;

SET @stock_quantity_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND column_name = 'stock_quantity'
);

SET @stock_quantity_sql = IF(
    @stock_quantity_exists = 0,
    'ALTER TABLE extra_services ADD COLUMN stock_quantity INT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE stock_quantity_stmt FROM @stock_quantity_sql;
EXECUTE stock_quantity_stmt;
DEALLOCATE PREPARE stock_quantity_stmt;

SET @remaining_quantity_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND column_name = 'remaining_quantity'
);

SET @remaining_quantity_sql = IF(
    @remaining_quantity_exists = 0,
    'ALTER TABLE extra_services ADD COLUMN remaining_quantity INT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE remaining_quantity_stmt FROM @remaining_quantity_sql;
EXECUTE remaining_quantity_stmt;
DEALLOCATE PREPARE remaining_quantity_stmt;

SET @unit_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND column_name = 'unit'
);

SET @unit_sql = IF(
    @unit_exists = 0,
    'ALTER TABLE extra_services ADD COLUMN unit VARCHAR(50) NULL',
    'SELECT 1'
);

PREPARE unit_stmt FROM @unit_sql;
EXECUTE unit_stmt;
DEALLOCATE PREPARE unit_stmt;

SET @stock_deducted_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'booking_extra_services'
      AND column_name = 'stock_deducted'
);

SET @stock_deducted_sql = IF(
    @stock_deducted_exists = 0,
    'ALTER TABLE booking_extra_services ADD COLUMN stock_deducted BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE stock_deducted_stmt FROM @stock_deducted_sql;
EXECUTE stock_deducted_stmt;
DEALLOCATE PREPARE stock_deducted_stmt;

SET @stock_returned_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'booking_extra_services'
      AND column_name = 'stock_returned'
);

SET @stock_returned_sql = IF(
    @stock_returned_exists = 0,
    'ALTER TABLE booking_extra_services ADD COLUMN stock_returned BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE stock_returned_stmt FROM @stock_returned_sql;
EXECUTE stock_returned_stmt;
DEALLOCATE PREPARE stock_returned_stmt;
