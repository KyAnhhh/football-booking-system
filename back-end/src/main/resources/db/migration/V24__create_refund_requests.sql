ALTER TABLE payments MODIFY COLUMN payment_status VARCHAR(40) NOT NULL;
CREATE TABLE IF NOT EXISTS refund_requests (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 payment_id BIGINT NOT NULL, user_id BIGINT NOT NULL,
 order_id BIGINT NULL, booking_id BIGINT NULL,
 amount DECIMAL(15,2) NOT NULL, reason VARCHAR(500) NULL,
 status VARCHAR(40) NOT NULL, bank_code VARCHAR(50) NULL,
 bank_account_number VARCHAR(50) NULL, bank_account_name VARCHAR(150) NULL,
 refund_transaction_code VARCHAR(100) NULL, admin_note VARCHAR(500) NULL,
 failure_reason VARCHAR(500) NULL, processed_by BIGINT NULL,
 requested_at DATETIME(6) NOT NULL, processed_at DATETIME(6) NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,
 CONSTRAINT uk_refund_payment UNIQUE (payment_id),
 CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
 CONSTRAINT fk_refund_user FOREIGN KEY (user_id) REFERENCES users(id),
 CONSTRAINT fk_refund_processed_by FOREIGN KEY (processed_by) REFERENCES users(id)
);
