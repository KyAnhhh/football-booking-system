ALTER TABLE stock_imports
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
    ADD COLUMN posted_at DATETIME(6) NULL,
    ADD COLUMN cancelled_at DATETIME(6) NULL;

UPDATE stock_imports
SET import_code = CONCAT('LEGACY-IMP-', id)
WHERE import_code IS NULL OR TRIM(import_code) = '';

UPDATE stock_imports
SET posted_at = created_at
WHERE status = 'POSTED' AND posted_at IS NULL;

ALTER TABLE stock_imports
    MODIFY COLUMN import_code VARCHAR(100) NOT NULL,
    ADD CONSTRAINT uk_stock_imports_import_code UNIQUE (import_code);

CREATE INDEX idx_stock_imports_status_created
    ON stock_imports(status, created_at);

CREATE INDEX idx_stock_imports_supplier_created
    ON stock_imports(supplier_id, created_at);
