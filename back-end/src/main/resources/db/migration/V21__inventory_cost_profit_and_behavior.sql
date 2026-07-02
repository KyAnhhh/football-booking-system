ALTER TABLE inventory_items
    ADD COLUMN inventory_behavior VARCHAR(30) NOT NULL DEFAULT 'CONSUMABLE';

UPDATE inventory_items
SET inventory_behavior = CASE
    WHEN item_type = 'PRODUCT_VARIANT' THEN 'SELLABLE'
    WHEN item_type = 'EXTRA_SERVICE' THEN 'CONSUMABLE'
    WHEN item_type = 'FIELD_ASSET' THEN 'RENTAL'
    ELSE 'CONSUMABLE'
END;

ALTER TABLE inventory_stocks
    ADD COLUMN average_unit_cost DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ADD COLUMN inventory_value DECIMAL(19, 2) NOT NULL DEFAULT 0;

ALTER TABLE order_items
    ADD COLUMN unit_cost DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ADD COLUMN total_cost DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ADD COLUMN gross_profit DECIMAL(19, 2) NOT NULL DEFAULT 0;

ALTER TABLE booking_extra_services
    ADD COLUMN unit_cost DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ADD COLUMN total_cost DECIMAL(19, 2) NOT NULL DEFAULT 0,
    ADD COLUMN gross_profit DECIMAL(19, 2) NOT NULL DEFAULT 0;

CREATE INDEX idx_inventory_items_behavior
    ON inventory_items(inventory_behavior);
