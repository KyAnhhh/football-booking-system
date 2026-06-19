SET @voucher_usage_refunded_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'voucher_usages'
      AND column_name = 'is_refunded'
);

SET @voucher_usage_refunded_sql = IF(
    @voucher_usage_refunded_exists = 0,
    'ALTER TABLE voucher_usages ADD COLUMN is_refunded BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE voucher_usage_refunded_stmt FROM @voucher_usage_refunded_sql;
EXECUTE voucher_usage_refunded_stmt;
DEALLOCATE PREPARE voucher_usage_refunded_stmt;

SET @voucher_usage_refunded_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'voucher_usages'
      AND column_name = 'refunded_at'
);

SET @voucher_usage_refunded_at_sql = IF(
    @voucher_usage_refunded_at_exists = 0,
    'ALTER TABLE voucher_usages ADD COLUMN refunded_at DATETIME(6) NULL',
    'SELECT 1'
);

PREPARE voucher_usage_refunded_at_stmt FROM @voucher_usage_refunded_at_sql;
EXECUTE voucher_usage_refunded_at_stmt;
DEALLOCATE PREPARE voucher_usage_refunded_at_stmt;
