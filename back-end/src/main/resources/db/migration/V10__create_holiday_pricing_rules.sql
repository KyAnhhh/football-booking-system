CREATE TABLE IF NOT EXISTS holiday_pricing_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_id BIGINT NOT NULL,
    holiday_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    price_per_slot DECIMAL(19, 2) NOT NULL,
    slot_duration INT NOT NULL DEFAULT 90,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_holiday_pricing_rules_field
        FOREIGN KEY (field_id) REFERENCES football_fields(id),
    CONSTRAINT fk_holiday_pricing_rules_holiday
        FOREIGN KEY (holiday_id) REFERENCES holidays(id)
);

CREATE INDEX idx_holiday_pricing_field_holiday
    ON holiday_pricing_rules(field_id, holiday_id);

CREATE INDEX idx_holiday_pricing_lookup
    ON holiday_pricing_rules(field_id, start_time, end_time, is_active);
