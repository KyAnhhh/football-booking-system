CREATE TABLE IF NOT EXISTS extra_services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255),
    price DECIMAL(19, 2),
    is_active BIT,
    PRIMARY KEY (id)
);

SET @extra_service_price_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'bookings'
      AND column_name = 'extra_service_price'
);

SET @extra_service_price_sql = IF(
    @extra_service_price_exists = 0,
    'ALTER TABLE bookings ADD COLUMN extra_service_price DECIMAL(19, 2) NULL',
    'SELECT 1'
);

PREPARE extra_service_price_stmt FROM @extra_service_price_sql;
EXECUTE extra_service_price_stmt;
DEALLOCATE PREPARE extra_service_price_stmt;

CREATE TABLE IF NOT EXISTS booking_extra_services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT,
    extra_service_id BIGINT,
    quantity INT,
    unit_price DECIMAL(19, 2),
    total_price DECIMAL(19, 2),
    PRIMARY KEY (id),
    INDEX idx_booking_extra_booking_id (booking_id),
    INDEX idx_booking_extra_service_id (extra_service_id),
    CONSTRAINT fk_booking_extra_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT fk_booking_extra_service
        FOREIGN KEY (extra_service_id) REFERENCES extra_services (id)
);
