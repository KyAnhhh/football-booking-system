SET @product_variant_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'product_variants'
      AND column_name = 'inventory_item_id'
);

SET @product_variant_inventory_item_sql = IF(
    @product_variant_inventory_item_exists = 0,
    'ALTER TABLE product_variants ADD COLUMN inventory_item_id BIGINT NULL',
    'SELECT 1'
);

PREPARE product_variant_inventory_item_stmt FROM @product_variant_inventory_item_sql;
EXECUTE product_variant_inventory_item_stmt;
DEALLOCATE PREPARE product_variant_inventory_item_stmt;

SET @extra_service_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND column_name = 'inventory_item_id'
);

SET @extra_service_inventory_item_sql = IF(
    @extra_service_inventory_item_exists = 0,
    'ALTER TABLE extra_services ADD COLUMN inventory_item_id BIGINT NULL',
    'SELECT 1'
);

PREPARE extra_service_inventory_item_stmt FROM @extra_service_inventory_item_sql;
EXECUTE extra_service_inventory_item_stmt;
DEALLOCATE PREPARE extra_service_inventory_item_stmt;

SET @order_item_stock_deducted_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'order_items'
      AND column_name = 'stock_deducted'
);

SET @order_item_stock_deducted_sql = IF(
    @order_item_stock_deducted_exists = 0,
    'ALTER TABLE order_items ADD COLUMN stock_deducted BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE order_item_stock_deducted_stmt FROM @order_item_stock_deducted_sql;
EXECUTE order_item_stock_deducted_stmt;
DEALLOCATE PREPARE order_item_stock_deducted_stmt;

SET @order_item_stock_returned_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'order_items'
      AND column_name = 'stock_returned'
);

SET @order_item_stock_returned_sql = IF(
    @order_item_stock_returned_exists = 0,
    'ALTER TABLE order_items ADD COLUMN stock_returned BIT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE order_item_stock_returned_stmt FROM @order_item_stock_returned_sql;
EXECUTE order_item_stock_returned_stmt;
DEALLOCATE PREPARE order_item_stock_returned_stmt;

SET @booking_extra_inventory_transaction_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'booking_extra_services'
      AND column_name = 'inventory_transaction_id'
);

SET @booking_extra_inventory_transaction_sql = IF(
    @booking_extra_inventory_transaction_exists = 0,
    'ALTER TABLE booking_extra_services ADD COLUMN inventory_transaction_id BIGINT NULL',
    'SELECT 1'
);

PREPARE booking_extra_inventory_transaction_stmt FROM @booking_extra_inventory_transaction_sql;
EXECUTE booking_extra_inventory_transaction_stmt;
DEALLOCATE PREPARE booking_extra_inventory_transaction_stmt;

SET @fk_product_variants_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'product_variants'
      AND constraint_name = 'fk_product_variants_inventory_item'
);

SET @fk_product_variants_inventory_item_sql = IF(
    @fk_product_variants_inventory_item_exists = 0,
    'ALTER TABLE product_variants ADD CONSTRAINT fk_product_variants_inventory_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)',
    'SELECT 1'
);

PREPARE fk_product_variants_inventory_item_stmt FROM @fk_product_variants_inventory_item_sql;
EXECUTE fk_product_variants_inventory_item_stmt;
DEALLOCATE PREPARE fk_product_variants_inventory_item_stmt;

SET @fk_extra_services_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND constraint_name = 'fk_extra_services_inventory_item'
);

SET @fk_extra_services_inventory_item_sql = IF(
    @fk_extra_services_inventory_item_exists = 0,
    'ALTER TABLE extra_services ADD CONSTRAINT fk_extra_services_inventory_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)',
    'SELECT 1'
);

PREPARE fk_extra_services_inventory_item_stmt FROM @fk_extra_services_inventory_item_sql;
EXECUTE fk_extra_services_inventory_item_stmt;
DEALLOCATE PREPARE fk_extra_services_inventory_item_stmt;

SET @fk_booking_extra_inventory_transaction_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'booking_extra_services'
      AND constraint_name = 'fk_booking_extra_services_inventory_transaction'
);

SET @fk_booking_extra_inventory_transaction_sql = IF(
    @fk_booking_extra_inventory_transaction_exists = 0,
    'ALTER TABLE booking_extra_services ADD CONSTRAINT fk_booking_extra_services_inventory_transaction FOREIGN KEY (inventory_transaction_id) REFERENCES inventory_transactions(id)',
    'SELECT 1'
);

PREPARE fk_booking_extra_inventory_transaction_stmt FROM @fk_booking_extra_inventory_transaction_sql;
EXECUTE fk_booking_extra_inventory_transaction_stmt;
DEALLOCATE PREPARE fk_booking_extra_inventory_transaction_stmt;

SET @idx_product_variants_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'product_variants'
      AND index_name = 'idx_product_variants_inventory_item'
);

SET @idx_product_variants_inventory_item_sql = IF(
    @idx_product_variants_inventory_item_exists = 0,
    'CREATE INDEX idx_product_variants_inventory_item ON product_variants(inventory_item_id)',
    'SELECT 1'
);

PREPARE idx_product_variants_inventory_item_stmt FROM @idx_product_variants_inventory_item_sql;
EXECUTE idx_product_variants_inventory_item_stmt;
DEALLOCATE PREPARE idx_product_variants_inventory_item_stmt;

SET @idx_extra_services_inventory_item_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'extra_services'
      AND index_name = 'idx_extra_services_inventory_item'
);

SET @idx_extra_services_inventory_item_sql = IF(
    @idx_extra_services_inventory_item_exists = 0,
    'CREATE INDEX idx_extra_services_inventory_item ON extra_services(inventory_item_id)',
    'SELECT 1'
);

PREPARE idx_extra_services_inventory_item_stmt FROM @idx_extra_services_inventory_item_sql;
EXECUTE idx_extra_services_inventory_item_stmt;
DEALLOCATE PREPARE idx_extra_services_inventory_item_stmt;
