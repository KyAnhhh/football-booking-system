SET @fk_maintenances_field_exists = (
    SELECT COUNT(*)
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND constraint_name = 'fk_maintenances_field'
);

SET @fk_maintenances_field_sql = IF(
    @fk_maintenances_field_exists = 0,
    'ALTER TABLE maintenances
        ADD CONSTRAINT fk_maintenances_field
        FOREIGN KEY (field_id) REFERENCES football_fields(id)',
    'SELECT 1'
);

PREPARE fk_maintenances_field_stmt FROM @fk_maintenances_field_sql;
EXECUTE fk_maintenances_field_stmt;
DEALLOCATE PREPARE fk_maintenances_field_stmt;

SET @fk_voucher_usages_voucher_exists = (
    SELECT COUNT(*)
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND constraint_name IN (
          'fk_voucher_usage_voucher',
          'fk_voucher_usages_voucher'
      )
);

SET @fk_voucher_usages_voucher_sql = IF(
    @fk_voucher_usages_voucher_exists = 0,
    'ALTER TABLE voucher_usages
        ADD CONSTRAINT fk_voucher_usages_voucher
        FOREIGN KEY (voucher_id) REFERENCES vouchers(id)',
    'SELECT 1'
);

PREPARE fk_voucher_usages_voucher_stmt FROM @fk_voucher_usages_voucher_sql;
EXECUTE fk_voucher_usages_voucher_stmt;
DEALLOCATE PREPARE fk_voucher_usages_voucher_stmt;

SET @fk_voucher_usages_booking_exists = (
    SELECT COUNT(*)
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND constraint_name IN (
          'fk_voucher_usage_booking',
          'fk_voucher_usages_booking'
      )
);

SET @fk_voucher_usages_booking_sql = IF(
    @fk_voucher_usages_booking_exists = 0,
    'ALTER TABLE voucher_usages
        ADD CONSTRAINT fk_voucher_usages_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)',
    'SELECT 1'
);

PREPARE fk_voucher_usages_booking_stmt FROM @fk_voucher_usages_booking_sql;
EXECUTE fk_voucher_usages_booking_stmt;
DEALLOCATE PREPARE fk_voucher_usages_booking_stmt;

SET @fk_voucher_usages_user_exists = (
    SELECT COUNT(*)
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND constraint_name IN (
          'fk_voucher_usage_user',
          'fk_voucher_usages_user'
      )
);

SET @fk_voucher_usages_user_sql = IF(
    @fk_voucher_usages_user_exists = 0,
    'ALTER TABLE voucher_usages
        ADD CONSTRAINT fk_voucher_usages_user
        FOREIGN KEY (user_id) REFERENCES users(id)',
    'SELECT 1'
);

PREPARE fk_voucher_usages_user_stmt FROM @fk_voucher_usages_user_sql;
EXECUTE fk_voucher_usages_user_stmt;
DEALLOCATE PREPARE fk_voucher_usages_user_stmt;
