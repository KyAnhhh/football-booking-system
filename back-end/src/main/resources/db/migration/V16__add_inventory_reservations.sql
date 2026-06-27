CREATE TABLE IF NOT EXISTS inventory_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    reference_type VARCHAR(50) NOT NULL,
    reference_id BIGINT NOT NULL,
    reference_line_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    reserved_at DATETIME NOT NULL,
    expires_at DATETIME NULL,
    committed_at DATETIME NULL,
    released_at DATETIME NULL,
    created_by BIGINT NULL,
    CONSTRAINT uk_inventory_reservation_reference_line
        UNIQUE (
            inventory_item_id,
            reference_type,
            reference_id,
            reference_line_id
        ),
    CONSTRAINT fk_inventory_reservations_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
    CONSTRAINT fk_inventory_reservations_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_inventory_reservations_status_expires
    ON inventory_reservations(status, expires_at);
CREATE INDEX idx_inventory_reservations_reference
    ON inventory_reservations(reference_type, reference_id);

ALTER TABLE inventory_transactions
    ADD COLUMN before_reserved_quantity INT NULL,
    ADD COLUMN after_reserved_quantity INT NULL;

ALTER TABLE order_items
    ADD COLUMN stock_reserved BIT NOT NULL DEFAULT 0,
    ADD COLUMN inventory_reservation_id BIGINT NULL,
    ADD CONSTRAINT fk_order_items_inventory_reservation
        FOREIGN KEY (inventory_reservation_id)
        REFERENCES inventory_reservations(id);

ALTER TABLE booking_extra_services
    ADD COLUMN stock_reserved BIT NOT NULL DEFAULT 0,
    ADD COLUMN inventory_reservation_id BIGINT NULL,
    ADD CONSTRAINT fk_booking_extra_inventory_reservation
        FOREIGN KEY (inventory_reservation_id)
        REFERENCES inventory_reservations(id);
