SET @product_image_sort_order_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'product_images'
      AND column_name = 'sort_order'
);

SET @add_product_image_sort_order_sql = IF(
    @product_image_sort_order_exists = 0,
    'ALTER TABLE product_images ADD COLUMN sort_order INT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE add_product_image_sort_order_stmt FROM @add_product_image_sort_order_sql;
EXECUTE add_product_image_sort_order_stmt;
DEALLOCATE PREPARE add_product_image_sort_order_stmt;

CREATE TABLE IF NOT EXISTS football_field_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_primary BIT NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_football_field_images_field
        FOREIGN KEY (field_id) REFERENCES football_fields(id)
);

CREATE INDEX idx_football_field_images_field
    ON football_field_images(field_id);
