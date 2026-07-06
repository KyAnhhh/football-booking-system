CREATE TABLE IF NOT EXISTS wishlist (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_wishlist_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_wishlist_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

SET @wishlist_status_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'wishlist'
      AND column_name = 'status'
);

SET @wishlist_drop_status_sql = IF(
    @wishlist_status_exists > 0,
    'ALTER TABLE wishlist DROP COLUMN status',
    'SELECT 1'
);

PREPARE wishlist_drop_status_stmt FROM @wishlist_drop_status_sql;
EXECUTE wishlist_drop_status_stmt;
DEALLOCATE PREPARE wishlist_drop_status_stmt;

SET @wishlist_updated_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'wishlist'
      AND column_name = 'updated_at'
);

SET @wishlist_drop_updated_at_sql = IF(
    @wishlist_updated_at_exists > 0,
    'ALTER TABLE wishlist DROP COLUMN updated_at',
    'SELECT 1'
);

PREPARE wishlist_drop_updated_at_stmt FROM @wishlist_drop_updated_at_sql;
EXECUTE wishlist_drop_updated_at_stmt;
DEALLOCATE PREPARE wishlist_drop_updated_at_stmt;

SET @wishlist_unique_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'wishlist'
      AND index_name = 'uk_wishlist_user_product'
);

SET @wishlist_unique_sql = IF(
    @wishlist_unique_exists = 0,
    'ALTER TABLE wishlist ADD CONSTRAINT uk_wishlist_user_product UNIQUE (user_id, product_id)',
    'SELECT 1'
);

PREPARE wishlist_unique_stmt FROM @wishlist_unique_sql;
EXECUTE wishlist_unique_stmt;
DEALLOCATE PREPARE wishlist_unique_stmt;
