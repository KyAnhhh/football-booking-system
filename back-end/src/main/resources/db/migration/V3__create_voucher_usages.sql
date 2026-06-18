CREATE TABLE IF NOT EXISTS voucher_usages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    voucher_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    voucher_code VARCHAR(255) NOT NULL,
    discount_amount DECIMAL(19, 2) NOT NULL,
    used_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_voucher_usage_booking UNIQUE (booking_id),
    INDEX idx_voucher_usage_voucher_id (voucher_id),
    INDEX idx_voucher_usage_user_id (user_id),
    CONSTRAINT fk_voucher_usage_voucher
        FOREIGN KEY (voucher_id) REFERENCES vouchers (id),
    CONSTRAINT fk_voucher_usage_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT fk_voucher_usage_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);
