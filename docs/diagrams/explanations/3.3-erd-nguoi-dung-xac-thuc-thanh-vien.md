# ERD-01 User, Auth, Membership

## Mục đích

Sơ đồ này mô tả cụm dữ liệu người dùng, hồ sơ giao hàng, token đặt lại mật khẩu, hạng thành viên và thông báo.

## Bảng/entity

- `USER`
- `MEMBERSHIP_TIER`
- `SHIPPING_ADDRESS`
- `PASSWORD_RESET_TOKEN`
- `NOTIFICATION`

## Quan hệ chính

- Một `MEMBERSHIP_TIER` phân loại nhiều `USER`.
- Một `USER` có nhiều `SHIPPING_ADDRESS`.
- Một `USER` có nhiều `PASSWORD_RESET_TOKEN`.
- Một `USER` nhận nhiều `NOTIFICATION`.

## Field đã rút gọn

Sơ đồ giữ các field quan trọng cho báo cáo: `id`, thông tin định danh, role/status, loyalty points, thời gian tạo/cập nhật và các field nghiệp vụ chính. `Role` và các loại thông báo là enum nên được biểu diễn bằng field dạng chuỗi. `RedisToken` không đưa vào ERD MySQL vì entity này dùng `@RedisHash`, không phải bảng quan hệ.

## Đoạn đưa vào báo cáo

Cụm dữ liệu người dùng quản lý thông tin tài khoản, phân quyền cơ bản và trạng thái hoạt động của người dùng trong hệ thống. Mỗi người dùng có thể có nhiều địa chỉ giao hàng để phục vụ quy trình checkout. Hệ thống lưu token đặt lại mật khẩu để hỗ trợ chức năng quên mật khẩu. Điểm tích lũy của người dùng được dùng để xác định hạng thành viên thông qua bảng `MEMBERSHIP_TIER`. Các thông báo được liên kết với người dùng để hiển thị sự kiện liên quan đến đơn hàng, booking hoặc các hoạt động hệ thống.
