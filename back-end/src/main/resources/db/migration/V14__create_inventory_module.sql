CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    reference_id BIGINT NULL,
    unit VARCHAR(50) NULL,
    active BIT NOT NULL DEFAULT 1,
    low_stock_threshold INT NOT NULL DEFAULT 0,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT uk_inventory_items_item_code UNIQUE (item_code)
);

CREATE TABLE IF NOT EXISTS inventory_stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    reserved_quantity INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0,
    updated_at DATETIME NULL,
    CONSTRAINT uk_inventory_stocks_item UNIQUE (inventory_item_id),
    CONSTRAINT fk_inventory_stocks_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)
);

CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NULL,
    email VARCHAR(255) NULL,
    address VARCHAR(500) NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NULL,
    updated_at DATETIME NULL
);

CREATE TABLE IF NOT EXISTS stock_imports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_code VARCHAR(100) NULL,
    supplier_id BIGINT NULL,
    total_amount DECIMAL(19, 2) NULL DEFAULT 0,
    note VARCHAR(500) NULL,
    created_by BIGINT NULL,
    created_at DATETIME NULL,
    CONSTRAINT fk_stock_imports_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_stock_imports_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS stock_import_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_import_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(19, 2) NULL DEFAULT 0,
    total_cost DECIMAL(19, 2) NULL DEFAULT 0,
    CONSTRAINT fk_stock_import_items_import
        FOREIGN KEY (stock_import_id) REFERENCES stock_imports(id),
    CONSTRAINT fk_stock_import_items_inventory_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id)
);

CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    before_quantity INT NOT NULL,
    after_quantity INT NOT NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT NULL,
    note VARCHAR(500) NULL,
    created_by BIGINT NULL,
    created_at DATETIME NULL,
    CONSTRAINT fk_inventory_transactions_item
        FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id),
    CONSTRAINT fk_inventory_transactions_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_inventory_items_type ON inventory_items(item_type);
CREATE INDEX idx_inventory_transactions_item ON inventory_transactions(inventory_item_id);
CREATE INDEX idx_inventory_transactions_created_at ON inventory_transactions(created_at);
