# SEQ-05 - Cancel and Manual Refund Flow

## Mục đích

Sơ đồ mô tả luồng hủy order/booking và xử lý hoàn tiền thủ công khi khách đã hoặc có thể đã chuyển khoản.

## Nội dung chính

- Client hủy order hoặc booking.
- Hệ thống hoàn lại tồn kho hoặc giải phóng reservation.
- Booking có voucher sẽ được hoàn usage.
- Nếu payment `UNPAID`, payment chuyển `FAILED` và không tạo refund.
- Nếu payment `PENDING`, refund chuyển `PAYMENT_REVIEW` để admin kiểm tra đã nhận tiền hay chưa.
- Nếu payment `PAID`, payment chuyển `REFUND_PENDING` và refund chờ khách cung cấp tài khoản.
- Admin xử lý refund đến `COMPLETED`, payment chuyển `REFUNDED`.

## Đoạn mô tả đưa vào báo cáo

Sơ đồ sequence cancel and refund thể hiện cách hệ thống xử lý tình huống khách hủy đơn hàng hoặc lịch đặt sân. Tùy theo trạng thái payment, hệ thống quyết định không tạo refund, tạo refund để admin kiểm tra tiền, hoặc chuyển thẳng sang chờ thông tin hoàn tiền. Khách hàng cung cấp tài khoản nhận hoàn, sau đó admin xử lý chuyển khoản thủ công và nhập mã giao dịch hoàn tiền. Flow này giúp hệ thống kiểm soát an toàn trường hợp khách đã chuyển khoản nhưng giao dịch chưa được xác nhận tự động.
