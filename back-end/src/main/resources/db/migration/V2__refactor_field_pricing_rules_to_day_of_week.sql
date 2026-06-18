ALTER TABLE field_pricing_rules
    ADD COLUMN day_of_week VARCHAR(20) NULL;

INSERT INTO field_pricing_rules (
    field_id,
    day_of_week,
    start_time,
    end_time,
    price_per_hour,
    is_active
)
SELECT
    field_id,
    weekday.day_of_week,
    start_time,
    end_time,
    price_per_hour,
    is_active
FROM field_pricing_rules
JOIN (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) weekday
WHERE pricing_type = 'WEEKDAY';

INSERT INTO field_pricing_rules (
    field_id,
    day_of_week,
    start_time,
    end_time,
    price_per_hour,
    is_active
)
SELECT
    field_id,
    weekend.day_of_week,
    start_time,
    end_time,
    price_per_hour,
    is_active
FROM field_pricing_rules
JOIN (
    SELECT 'SATURDAY' AS day_of_week
    UNION ALL SELECT 'SUNDAY'
) weekend
WHERE pricing_type = 'WEEKEND';

CREATE TABLE IF NOT EXISTS field_pricing_rules_holiday_backup AS
SELECT *
FROM field_pricing_rules
WHERE 1 = 0;

INSERT INTO field_pricing_rules_holiday_backup
SELECT *
FROM field_pricing_rules
WHERE pricing_type = 'HOLIDAY';

DELETE FROM field_pricing_rules
WHERE pricing_type IN ('WEEKDAY', 'WEEKEND', 'HOLIDAY');

ALTER TABLE field_pricing_rules
    MODIFY COLUMN day_of_week VARCHAR(20) NOT NULL;

ALTER TABLE field_pricing_rules
    DROP COLUMN pricing_type;
