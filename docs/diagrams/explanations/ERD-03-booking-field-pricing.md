# ERD-03 Booking, Field, Pricing

## Mục đích

Sơ đồ này mô tả cụm dữ liệu đặt sân: sân bóng, ảnh sân, booking, dịch vụ thêm, bảo trì, pricing rule, ngày lễ, voucher, lịch sử trạng thái và đánh giá booking.

## Bảng/entity

- `FOOTBALL_FIELD`
- `FOOTBALL_FIELD_IMAGE`
- `BOOKING`
- `BOOKING_EXTRA_SERVICE`
- `EXTRA_SERVICE`
- `MAINTENANCE`
- `FIELD_PRICING_RULE`
- `HOLIDAY`
- `HOLIDAY_PRICING_RULE`
- `VOUCHER`
- `VOUCHER_USAGE`
- `BOOKING_REVIEW`
- `BOOKING_STATUS_HISTORY`
- `USER` được vẽ dạng rút gọn để thể hiện liên kết.

## Quan hệ chính

- Một `FOOTBALL_FIELD` có nhiều ảnh, nhiều booking, nhiều lịch bảo trì và nhiều rule giá.
- `BOOKING` thuộc về một `USER` và một `FOOTBALL_FIELD`.
- `BOOKING` có nhiều `BOOKING_EXTRA_SERVICE`, mỗi dòng liên kết tới một `EXTRA_SERVICE`.
- `VOUCHER_USAGE` ghi nhận voucher được dùng bởi user cho booking.
- `BOOKING_STATUS_HISTORY` lưu lịch sử chuyển trạng thái booking và người thực hiện.

## Field đã rút gọn

Các field giữ lại gồm thời gian đặt sân, giá gốc, giá sau giảm, trạng thái, mã voucher, thông tin dịch vụ thêm, rule giá theo ngày/giờ và thông tin bảo trì. Các field chi phí nội bộ như cost/profit của dịch vụ thêm được rút gọn để diagram vừa A4.

## Đoạn đưa vào báo cáo

Cụm booking quản lý toàn bộ vòng đời đặt sân từ chọn sân, chọn khung giờ, áp dụng giá, chọn dịch vụ thêm đến đánh giá sau khi sử dụng. Bảng `BOOKING` lưu thời gian bắt đầu, kết thúc, tổng tiền và các khoản giảm giá từ membership hoặc voucher. Các rule giá theo ngày thường và ngày lễ cho phép hệ thống tính giá linh hoạt theo khung giờ. Lịch bảo trì được liên kết với sân để loại trừ các khung giờ không thể đặt. Lịch sử trạng thái giúp admin theo dõi quá trình xác nhận, check-in, check-out hoặc hủy booking.
