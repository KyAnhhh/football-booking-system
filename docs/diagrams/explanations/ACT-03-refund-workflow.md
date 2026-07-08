# ACT-03 - Refund Workflow

## Mục đích

Sơ đồ activity mô tả trạng thái và quyết định nghiệp vụ trong quy trình hoàn tiền thủ công.

## Nội dung chính

- `UNPAID` khi hủy sẽ chuyển payment sang `FAILED`, không tạo refund.
- `PENDING` khi hủy sẽ tạo refund `PAYMENT_REVIEW`.
- Admin xác nhận đã nhận tiền thì refund chuyển `AWAITING_INFORMATION`.
- Client nhập tài khoản, refund chuyển `PENDING`.
- Admin xử lý, refund chuyển `PROCESSING`.
- Hoàn tất thì refund `COMPLETED`, payment `REFUNDED`.
- Nếu lỗi chuyển khoản, refund chuyển `FAILED`.

## Đoạn mô tả đưa vào báo cáo

Sơ đồ activity refund workflow trình bày các trạng thái chính trong quy trình hoàn tiền. Khi khách hủy đơn hoặc booking, hệ thống phân nhánh theo trạng thái thanh toán để quyết định có cần tạo refund hay không. Với giao dịch đang chờ xác nhận, admin phải kiểm tra tiền trước khi yêu cầu khách cung cấp tài khoản nhận hoàn. Khi admin hoàn tất chuyển khoản thủ công, refund chuyển sang `COMPLETED` và payment được cập nhật thành `REFUNDED`.
