CREATE TABLE IF NOT EXISTS booking_status_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    old_status VARCHAR(40),
    new_status VARCHAR(40) NOT NULL,
    changed_by BIGINT,
    changed_by_name VARCHAR(120),
    changed_by_role VARCHAR(40),
    note VARCHAR(500),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_booking_status_histories_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_booking_status_histories_user
        FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE INDEX idx_booking_status_histories_booking_created
    ON booking_status_histories(booking_id, created_at);
