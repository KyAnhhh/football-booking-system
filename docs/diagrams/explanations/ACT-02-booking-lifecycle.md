# ACT-02 - Booking Lifecycle

## Mục đích

Sơ đồ activity mô tả vòng đời của một booking từ lúc khách chọn sân đến khi hoàn tất hoặc bị hủy/hết hạn.

## Nội dung chính

- Chọn sân, slot, extra service.
- Áp dụng membership và voucher.
- Tạo booking, payment và giữ tồn kho dịch vụ thêm.
- VietQR cần admin xác nhận trước khi booking được xác nhận.
- Booking đi qua các trạng thái `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`.
- Khi hủy/hết hạn, hệ thống hoàn voucher, stock và tạo refund nếu cần.

## Đoạn mô tả đưa vào báo cáo

Sơ đồ activity booking lifecycle mô tả toàn bộ vòng đời đặt sân trong hệ thống. Sau khi khách chọn sân, slot và dịch vụ thêm, hệ thống tính giá, áp dụng ưu đãi và tạo booking. Với thanh toán VietQR, booking chỉ được xác nhận sau khi admin xác nhận đã nhận tiền. Booking sau đó được vận hành qua check-in, check-out và hoàn tất; nếu booking bị hủy hoặc hết hạn, hệ thống giải phóng tài nguyên liên quan và xử lý refund khi cần.
