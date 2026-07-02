ALTER TABLE products
    ADD COLUMN publication_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT';

ALTER TABLE extra_services
    ADD COLUMN publication_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT';

CREATE INDEX idx_products_publication_status
    ON products(publication_status);

CREATE INDEX idx_extra_services_publication_status
    ON extra_services(publication_status);
