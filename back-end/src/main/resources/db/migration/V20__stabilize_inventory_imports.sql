ALTER TABLE stock_import_items
    ADD CONSTRAINT uk_stock_import_item
        UNIQUE (stock_import_id, inventory_item_id);

ALTER TABLE inventory_items
    ADD CONSTRAINT uk_inventory_item_reference
        UNIQUE (item_type, reference_id);

CREATE INDEX idx_inventory_items_name_code_type
    ON inventory_items(item_type, name, item_code);

CREATE INDEX idx_inventory_stocks_available
    ON inventory_stocks(available_quantity);
