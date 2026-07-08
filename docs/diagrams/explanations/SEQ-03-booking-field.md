# SEQ-03 - Football Field Booking Flow

## Mục đích

Sơ đồ mô tả luồng đặt sân bóng, bao gồm kiểm tra khung giờ, tính giá, áp dụng ưu đãi, chọn dịch vụ thêm và tạo payment.

## Nội dung chính

- Client chọn sân, ngày giờ, dịch vụ thêm và voucher.
- `BookingServiceImpl` kiểm tra thời gian và tình trạng khả dụng của sân.
- `PricingService` tính giá sân theo khung giờ.
- `MembershipService` và `VoucherService` áp dụng ưu đãi.
- Extra service có tồn kho sẽ được giữ thông qua `InventoryService`.
- Booking được tạo cùng payment trạng thái `UNPAID`.

## Đoạn mô tả đưa vào báo cáo

Sơ đồ sequence booking field trình bày nghiệp vụ đặt sân từ phía khách hàng. Hệ thống kiểm tra sân và khung giờ, tính giá bằng pricing rule, áp dụng giảm giá membership và voucher nếu có. Các dịch vụ thêm được xử lý cùng booking và có thể liên kết với tồn kho để giữ số lượng. Sau khi booking được lưu, hệ thống tạo payment ban đầu và cho phép khách gửi xác nhận chuyển khoản đối với VietQR.
