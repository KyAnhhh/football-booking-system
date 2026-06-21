-- Demo data for local development.
-- Run manually on the shopbongda database when you need a clean demo/test dataset.
-- Password for both demo accounts is: password

SET @now = NOW(6);
SET @password_hash = '$2a$10$7EqJtq98hPqEX7fNZaFWoO9uODSpNSnXqqXr4rkYr.GM081BQLSQe';

INSERT INTO membership_tiers (id, name, required_points, discount_percent, is_active)
VALUES
    (9001, 'Đồng', 0, 0, 1),
    (9002, 'Bạc', 500, 3, 1),
    (9003, 'Vàng', 1500, 5, 1),
    (9004, 'Kim cương', 3000, 8, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    required_points = VALUES(required_points),
    discount_percent = VALUES(discount_percent),
    is_active = VALUES(is_active);

INSERT INTO users (
    id,
    username,
    email,
    password,
    phone,
    role,
    status,
    loyalty_points,
    membership_tier_id,
    created_at,
    updated_at
)
VALUES
    (9001, 'demo_admin', 'demo.admin@shopbongda.local', @password_hash, '0900000001', 'ROLE_ADMIN', 'active', 0, 9001, @now, @now),
    (9002, 'demo_user', 'demo.user@shopbongda.local', @password_hash, '0900000002', 'ROLE_CLIENT', 'active', 1800, 9003, @now, @now)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    phone = VALUES(phone),
    role = VALUES(role),
    status = VALUES(status),
    loyalty_points = VALUES(loyalty_points),
    membership_tier_id = VALUES(membership_tier_id),
    updated_at = VALUES(updated_at);

INSERT INTO football_fields (
    id,
    field_name,
    field_type,
    image_url,
    price_per_hour,
    status,
    address,
    description,
    created_at,
    updated_at
)
VALUES
    (9001, 'Sân Cỏ Sao Mai 5A', 'FIVE_A_SIDE', 'https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=1200&q=80', 300000, 'AVAILABLE', '12 Nguyễn Văn Linh, Quận 7, TP.HCM', 'Sân 5 người cỏ nhân tạo, đèn LED, phù hợp đá tối.', @now, @now),
    (9002, 'Sân Bình Minh 5B', 'FIVE_A_SIDE', 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=1200&q=80', 320000, 'AVAILABLE', '88 Lê Văn Việt, TP. Thủ Đức, TP.HCM', 'Sân 5 người thoáng, có khu chờ và bãi xe.', @now, @now),
    (9003, 'Sân Thành Công 7A', 'SEVEN_A_SIDE', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80', 450000, 'AVAILABLE', '45 Hoàng Hoa Thám, Bình Thạnh, TP.HCM', 'Sân 7 người kích thước chuẩn, mặt cỏ mới.', @now, @now),
    (9004, 'Sân Phú Nhuận 7B', 'SEVEN_A_SIDE', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80', 480000, 'AVAILABLE', '21 Phan Đình Phùng, Phú Nhuận, TP.HCM', 'Sân 7 người có mái che khu nghỉ.', @now, @now),
    (9005, 'Sân Đại Thắng 11A', 'ELEVEN_A_SIDE', 'https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?auto=format&fit=crop&w=1200&q=80', 900000, 'AVAILABLE', 'Khu thể thao An Phú, TP. Thủ Đức, TP.HCM', 'Sân 11 người dành cho giải phong trào.', @now, @now),
    (9006, 'Sân Riverside 5C', 'FIVE_A_SIDE', 'https://images.unsplash.com/photo-1459865264687-595d652de67e?auto=format&fit=crop&w=1200&q=80', 350000, 'AVAILABLE', '6 Trần Não, TP. Thủ Đức, TP.HCM', 'Sân ven sông, ánh sáng tốt, có nước uống.', @now, @now)
ON DUPLICATE KEY UPDATE
    field_name = VALUES(field_name),
    field_type = VALUES(field_type),
    image_url = VALUES(image_url),
    price_per_hour = VALUES(price_per_hour),
    status = VALUES(status),
    address = VALUES(address),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO field_pricing_rules (
    id,
    field_id,
    day_of_week,
    start_time,
    end_time,
    price_per_slot,
    slot_duration,
    is_active
)
SELECT
    910000 + (f.field_order * 100) + (d.day_order * 10) + s.slot_order AS id,
    f.field_id,
    d.day_of_week,
    s.start_time,
    s.end_time,
    CASE
        WHEN f.field_type = 'ELEVEN_A_SIDE' THEN
            CASE
                WHEN d.day_of_week IN ('SATURDAY', 'SUNDAY') THEN s.base_price + 350000
                WHEN s.slot_order = 3 THEN s.base_price + 250000
                ELSE s.base_price + 150000
            END
        WHEN f.field_type = 'SEVEN_A_SIDE' THEN
            CASE
                WHEN d.day_of_week IN ('SATURDAY', 'SUNDAY') THEN s.base_price + 160000
                WHEN s.slot_order = 3 THEN s.base_price + 120000
                ELSE s.base_price + 60000
            END
        ELSE
            CASE
                WHEN d.day_of_week IN ('SATURDAY', 'SUNDAY') THEN s.base_price + 90000
                WHEN s.slot_order = 3 THEN s.base_price + 60000
                ELSE s.base_price
            END
    END AS price_per_slot,
    90,
    1
FROM (
    SELECT 1 AS field_order, 9001 AS field_id, 'FIVE_A_SIDE' AS field_type
    UNION ALL SELECT 2, 9002, 'FIVE_A_SIDE'
    UNION ALL SELECT 3, 9003, 'SEVEN_A_SIDE'
    UNION ALL SELECT 4, 9004, 'SEVEN_A_SIDE'
    UNION ALL SELECT 5, 9005, 'ELEVEN_A_SIDE'
    UNION ALL SELECT 6, 9006, 'FIVE_A_SIDE'
) f
CROSS JOIN (
    SELECT 1 AS day_order, 'MONDAY' AS day_of_week
    UNION ALL SELECT 2, 'TUESDAY'
    UNION ALL SELECT 3, 'WEDNESDAY'
    UNION ALL SELECT 4, 'THURSDAY'
    UNION ALL SELECT 5, 'FRIDAY'
    UNION ALL SELECT 6, 'SATURDAY'
    UNION ALL SELECT 7, 'SUNDAY'
) d
CROSS JOIN (
    SELECT 1 AS slot_order, CAST('06:00:00' AS TIME) AS start_time, CAST('07:30:00' AS TIME) AS end_time, 240000 AS base_price
    UNION ALL SELECT 2, CAST('15:00:00' AS TIME), CAST('16:30:00' AS TIME), 280000
    UNION ALL SELECT 3, CAST('18:00:00' AS TIME), CAST('19:30:00' AS TIME), 340000
    UNION ALL SELECT 4, CAST('19:30:00' AS TIME), CAST('21:00:00' AS TIME), 360000
) s
ON DUPLICATE KEY UPDATE
    field_id = VALUES(field_id),
    day_of_week = VALUES(day_of_week),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    price_per_slot = VALUES(price_per_slot),
    slot_duration = VALUES(slot_duration),
    is_active = VALUES(is_active);

INSERT INTO holidays (id, name, holiday_date, description, is_active, created_at, updated_at)
VALUES
    (9001, 'Ngày demo giá lễ', '2026-07-27', 'Dùng để kiểm tra pricing ngày lễ theo rule Chủ nhật.', 1, @now, @now),
    (9002, 'Ngày hội bóng đá', '2026-08-15', 'Ngày lễ nội bộ để test danh sách holiday.', 1, @now, @now)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    holiday_date = VALUES(holiday_date),
    description = VALUES(description),
    is_active = VALUES(is_active),
    updated_at = VALUES(updated_at);

INSERT INTO extra_services (
    id, name, description, price, is_active,
    track_stock, stock_quantity, remaining_quantity, unit
)
VALUES
    (9001, 'Thuê bóng thi đấu', 'Một quả bóng tiêu chuẩn cho ca đặt sân.', 50000, 1, 1, 20, 20, 'quả'),
    (9002, 'Nước suối thùng', 'Thùng 24 chai nước suối.', 90000, 1, 1, 50, 50, 'thùng'),
    (9003, 'Áo bib đội', 'Bộ 10 áo bib phân đội.', 70000, 1, 1, 30, 30, 'bộ'),
    (9004, 'Trọng tài', 'Trọng tài điều hành trận giao hữu.', 250000, 1, 0, 0, 0, 'lượt')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    price = VALUES(price),
    is_active = VALUES(is_active),
    track_stock = VALUES(track_stock),
    stock_quantity = VALUES(stock_quantity),
    remaining_quantity = VALUES(remaining_quantity),
    unit = VALUES(unit);

INSERT INTO vouchers (
    id,
    code,
    description,
    discount_type,
    discount_value,
    quantity,
    is_active,
    start_date,
    end_date
)
VALUES
    (9001, 'WELCOME10', 'Giảm 10% cho khách đặt sân demo.', 'PERCENT', 10, 50, 1, '2026-06-01 00:00:00', '2026-12-31 23:59:59'),
    (9002, 'FIELD50K', 'Giảm trực tiếp 50.000đ cho booking sân.', 'FIXED_AMOUNT', 50000, 30, 1, '2026-06-01 00:00:00', '2026-12-31 23:59:59'),
    (9003, 'WEEKEND5', 'Giảm 5% để test booking cuối tuần.', 'PERCENT', 5, 20, 1, '2026-06-01 00:00:00', '2026-12-31 23:59:59')
ON DUPLICATE KEY UPDATE
    code = VALUES(code),
    description = VALUES(description),
    discount_type = VALUES(discount_type),
    discount_value = VALUES(discount_value),
    quantity = VALUES(quantity),
    is_active = VALUES(is_active),
    start_date = VALUES(start_date),
    end_date = VALUES(end_date);

INSERT INTO maintenances (id, field_id, start_time, end_time, reason, description, created_at, updated_at)
VALUES
    (9001, 9001, '2026-07-02 18:00:00', '2026-07-02 19:30:00', 'Bảo trì đèn sân', 'Case demo: slot 18:00-19:30 của Sân Cỏ Sao Mai 5A phải bị chặn.', @now, @now),
    (9002, 9003, '2026-07-06 15:00:00', '2026-07-06 16:30:00', 'Vệ sinh mặt cỏ', 'Case demo: slot chiều của Sân Thành Công 7A phải bị chặn.', @now, @now)
ON DUPLICATE KEY UPDATE
    field_id = VALUES(field_id),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    reason = VALUES(reason),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO brands (id, name, description, logo_url, created_at, updated_at)
VALUES
    (9001, 'Nike Demo', 'Thương hiệu đồ bóng đá demo.', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=600&q=80', @now, @now),
    (9002, 'Adidas Demo', 'Thương hiệu đồ thể thao demo.', 'https://images.unsplash.com/photo-1556906781-9a412961c28c?auto=format&fit=crop&w=600&q=80', @now, @now),
    (9003, 'Puma Demo', 'Thương hiệu phụ kiện demo.', 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=600&q=80', @now, @now)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    logo_url = VALUES(logo_url),
    updated_at = VALUES(updated_at);

INSERT INTO categories (id, name, description, created_at, updated_at)
VALUES
    (9001, 'Giày bóng đá', 'Giày sân cỏ nhân tạo và sân cỏ tự nhiên.', @now, @now),
    (9002, 'Áo đấu', 'Áo bóng đá câu lạc bộ và đội tuyển.', @now, @now),
    (9003, 'Bóng thi đấu', 'Bóng tập luyện và thi đấu.', @now, @now),
    (9004, 'Phụ kiện', 'Tất, găng tay, băng bảo vệ và túi thể thao.', @now, @now)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO sizes (id, code, name, description, created_at, updated_at)
VALUES
    (9001, 'S', 'Size S', 'Phù hợp người nhỏ.', @now, @now),
    (9002, 'M', 'Size M', 'Phù hợp người trung bình.', @now, @now),
    (9003, 'L', 'Size L', 'Phù hợp người lớn.', @now, @now),
    (9004, 'XL', 'Size XL', 'Phù hợp người cao lớn.', @now, @now),
    (9005, '40', 'Size 40', 'Size giày 40.', @now, @now),
    (9006, '41', 'Size 41', 'Size giày 41.', @now, @now),
    (9007, '42', 'Size 42', 'Size giày 42.', @now, @now),
    (9008, 'STD', 'Tiêu chuẩn', 'Kích thước tiêu chuẩn.', @now, @now)
ON DUPLICATE KEY UPDATE
    code = VALUES(code),
    name = VALUES(name),
    description = VALUES(description),
    updated_at = VALUES(updated_at);

INSERT INTO products (id, name, description, discount, category_id, brand_id, created_at, updated_at)
VALUES
    (9001, 'Giày Nike Mercurial Demo', 'Giày tốc độ cho sân cỏ nhân tạo.', 12, 9001, 9001, @now, @now),
    (9002, 'Giày Adidas Predator Demo', 'Giày kiểm soát bóng cho tiền vệ.', 8, 9001, 9002, @now, @now),
    (9003, 'Giày Puma Future Demo', 'Giày ôm chân, phù hợp đá sân 5.', 10, 9001, 9003, @now, @now),
    (9004, 'Áo đấu sân nhà Demo', 'Áo đấu thoáng khí, màu đỏ.', 15, 9002, 9001, @now, @now),
    (9005, 'Áo đấu sân khách Demo', 'Áo đấu nhẹ, màu trắng.', 0, 9002, 9002, @now, @now),
    (9006, 'Áo thủ môn Demo', 'Áo thủ môn tay dài, có đệm nhẹ.', 5, 9002, 9003, @now, @now),
    (9007, 'Bóng thi đấu FIFA Demo', 'Bóng số 5 dùng cho trận đấu.', 7, 9003, 9002, @now, @now),
    (9008, 'Bóng tập luyện Demo', 'Bóng bền cho luyện tập hằng ngày.', 0, 9003, 9003, @now, @now),
    (9009, 'Găng tay thủ môn Demo', 'Găng tay bám bóng tốt.', 10, 9004, 9001, @now, @now),
    (9010, 'Tất chống trượt Demo', 'Tất đá bóng có hạt chống trượt.', 5, 9004, 9002, @now, @now),
    (9011, 'Băng bảo vệ ống đồng Demo', 'Bảo vệ ống đồng nhẹ và chắc.', 0, 9004, 9003, @now, @now),
    (9012, 'Túi thể thao Demo', 'Túi đựng giày và đồ tập.', 18, 9004, 9001, @now, @now),
    (9013, 'Áo training Demo', 'Áo tập luyện nhanh khô.', 9, 9002, 9002, @now, @now),
    (9014, 'Giày sân cỏ tự nhiên Demo', 'Đinh FG cho sân cỏ tự nhiên.', 6, 9001, 9001, @now, @now),
    (9015, 'Bóng futsal Demo', 'Bóng futsal nảy thấp.', 4, 9003, 9003, @now, @now),
    (9016, 'Bình nước thể thao Demo', 'Bình nước dung tích 750ml.', 0, 9004, 9002, @now, @now)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    discount = VALUES(discount),
    category_id = VALUES(category_id),
    brand_id = VALUES(brand_id),
    updated_at = VALUES(updated_at);

INSERT INTO product_variants (id, product_id, size_id, price, stock, sold_quantity)
SELECT
    variant_data.id,
    variant_data.product_id,
    variant_data.size_id,
    variant_data.price,
    variant_data.stock,
    variant_data.sold_quantity
FROM (
    SELECT
        920000 + (p.product_id - 9000) * 10 + s.size_order AS id,
        p.product_id,
        s.size_id,
        p.price,
        p.stock,
        0 AS sold_quantity
    FROM (
        SELECT 9001 AS product_id, 1890000 AS price, 20 AS stock
        UNION ALL SELECT 9002, 1750000, 18
        UNION ALL SELECT 9003, 1650000, 16
        UNION ALL SELECT 9004, 390000, 35
        UNION ALL SELECT 9005, 420000, 28
        UNION ALL SELECT 9006, 480000, 14
        UNION ALL SELECT 9007, 690000, 24
        UNION ALL SELECT 9008, 390000, 30
        UNION ALL SELECT 9009, 520000, 18
        UNION ALL SELECT 9010, 120000, 50
        UNION ALL SELECT 9011, 160000, 40
        UNION ALL SELECT 9012, 590000, 12
        UNION ALL SELECT 9013, 260000, 32
        UNION ALL SELECT 9014, 2150000, 10
        UNION ALL SELECT 9015, 450000, 22
        UNION ALL SELECT 9016, 150000, 45
    ) p
    JOIN (
        SELECT 1 AS size_order, 9005 AS size_id
        UNION ALL SELECT 2, 9006
        UNION ALL SELECT 3, 9007
    ) s ON p.product_id IN (9001, 9002, 9003, 9014)
    UNION ALL
    SELECT
        920000 + (p.product_id - 9000) * 10 + s.size_order,
        p.product_id,
        s.size_id,
        p.price,
        p.stock,
        0
    FROM (
        SELECT 9004 AS product_id, 390000 AS price, 35 AS stock
        UNION ALL SELECT 9005, 420000, 28
        UNION ALL SELECT 9006, 480000, 14
        UNION ALL SELECT 9013, 260000, 32
    ) p
    JOIN (
        SELECT 1 AS size_order, 9001 AS size_id
        UNION ALL SELECT 2, 9002
        UNION ALL SELECT 3, 9003
        UNION ALL SELECT 4, 9004
    ) s
    UNION ALL
    SELECT
        920000 + (p.product_id - 9000) * 10 + 8,
        p.product_id,
        9008,
        p.price,
        p.stock,
        0
    FROM (
        SELECT 9007 AS product_id, 690000 AS price, 24 AS stock
        UNION ALL SELECT 9008, 390000, 30
        UNION ALL SELECT 9009, 520000, 18
        UNION ALL SELECT 9010, 120000, 50
        UNION ALL SELECT 9011, 160000, 40
        UNION ALL SELECT 9012, 590000, 12
        UNION ALL SELECT 9015, 450000, 22
        UNION ALL SELECT 9016, 150000, 45
    ) p
) variant_data
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    size_id = VALUES(size_id),
    price = VALUES(price),
    stock = VALUES(stock),
    sold_quantity = VALUES(sold_quantity);

INSERT INTO product_images (id, image_url, is_primary, product_id)
VALUES
    (9301, 'https://images.unsplash.com/photo-1511886929837-354d827aae26?auto=format&fit=crop&w=900&q=80', 1, 9001),
    (9302, 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=900&q=80', 1, 9002),
    (9303, 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?auto=format&fit=crop&w=900&q=80', 1, 9003),
    (9304, 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 1, 9004),
    (9305, 'https://images.unsplash.com/photo-1503341504253-dff4815485f1?auto=format&fit=crop&w=900&q=80', 1, 9005),
    (9306, 'https://images.unsplash.com/photo-1577212017184-80cc0da11082?auto=format&fit=crop&w=900&q=80', 1, 9006),
    (9307, 'https://images.unsplash.com/photo-1614632537190-23e4146777db?auto=format&fit=crop&w=900&q=80', 1, 9007),
    (9308, 'https://images.unsplash.com/photo-1553778263-73a83bab9b0c?auto=format&fit=crop&w=900&q=80', 1, 9008),
    (9309, 'https://images.unsplash.com/photo-1519861531473-9200262188bf?auto=format&fit=crop&w=900&q=80', 1, 9009),
    (9310, 'https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?auto=format&fit=crop&w=900&q=80', 1, 9010),
    (9311, 'https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=900&q=80', 1, 9011),
    (9312, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80', 1, 9012),
    (9313, 'https://images.unsplash.com/photo-1578587018452-892bacefd3f2?auto=format&fit=crop&w=900&q=80', 1, 9013),
    (9314, 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=80', 1, 9014),
    (9315, 'https://images.unsplash.com/photo-1628891890377-57dba271018c?auto=format&fit=crop&w=900&q=80', 1, 9015),
    (9316, 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80', 1, 9016)
ON DUPLICATE KEY UPDATE
    image_url = VALUES(image_url),
    is_primary = VALUES(is_primary),
    product_id = VALUES(product_id);
