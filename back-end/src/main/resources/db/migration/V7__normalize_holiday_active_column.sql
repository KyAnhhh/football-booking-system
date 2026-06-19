SET @holiday_is_active_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'holidays'
      AND column_name = 'is_active'
);

SET @add_holiday_is_active_sql = IF(
    @holiday_is_active_exists = 0,
    'ALTER TABLE holidays ADD COLUMN is_active BIT NOT NULL DEFAULT b''1''',
    'SELECT 1'
);

PREPARE add_holiday_is_active_stmt FROM @add_holiday_is_active_sql;
EXECUTE add_holiday_is_active_stmt;
DEALLOCATE PREPARE add_holiday_is_active_stmt;

SET @holiday_active_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'holidays'
      AND column_name = 'active'
);

SET @copy_holiday_active_sql = IF(
    @holiday_active_exists > 0,
    'UPDATE holidays SET is_active = active WHERE active IS NOT NULL',
    'SELECT 1'
);

PREPARE copy_holiday_active_stmt FROM @copy_holiday_active_sql;
EXECUTE copy_holiday_active_stmt;
DEALLOCATE PREPARE copy_holiday_active_stmt;
