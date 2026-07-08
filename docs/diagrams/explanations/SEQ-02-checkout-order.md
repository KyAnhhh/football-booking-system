# SEQ-02 - Checkout Order Flow

## Mục đích

Sơ đồ mô tả luồng khách hàng checkout giỏ hàng, hệ thống tạo đơn hàng, tạo payment và giữ tồn kho tương ứng.

## Nội dung chính

- Client gửi yêu cầu checkout từ trang React.
- Backend tải user, cart, địa chỉ giao hàng và kiểm tra phương thức thanh toán.
- `OrderServiceImpl` tạo `Order`, `OrderItem` và `Payment` với trạng thái ban đầu `UNPAID`.
- Nếu sản phẩm có liên kết inventory, hệ thống gọi `InventoryService.reserveStock`.
- Với VietQR, client có thể gửi xác nhận đã chuyển khoản để payment chuyển sang `PENDING`.

## Đoạn mô tả đưa vào báo cáo

Sơ đồ sequence checkout order thể hiện quá trình xử lý đơn hàng từ khi khách hàng xác nhận giỏ hàng đến khi hệ thống tạo order và payment. Backend kiểm tra thông tin người dùng, địa chỉ giao hàng, sản phẩm trong giỏ và phương thức thanh toán. Sau khi tạo đơn hàng, hệ thống giữ tồn kho thông qua inventory reservation để tránh bán vượt số lượng còn lại. Nếu khách chọn VietQR, payment ban đầu là `UNPAID` và chuyển sang `PENDING` khi khách gửi yêu cầu kiểm tra chuyển khoản.
