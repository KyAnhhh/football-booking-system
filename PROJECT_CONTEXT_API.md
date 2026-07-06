# Project Context — API

> Cập nhật gần nhất: 2026-07-01. Đọc phần **CẬP NHẬT API** ở cuối file trước.

Base URL frontend đang dùng: `http://localhost:8080`.

Quyền: `PUB` không cần token; `USER` là client/admin/manager/staff; `STAFF+`; `MANAGER+`; `ADMIN`.

## Auth và user

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| POST | `/api/auth/login` | PUB | Đăng nhập |
| POST | `/api/auth/register` | PUB | Đăng ký |
| POST | `/api/auth/logout` | token | Thu hồi access JWT |
| POST | `/api/auth/refresh` | PUB + refresh token | Rotate token |
| POST | `/api/auth/forgot-password` | PUB | Gửi link reset |
| POST | `/api/auth/reset-password` | PUB | Đặt mật khẩu mới |
| POST/GET/PUT/DELETE | `/api/admin/users[/{id}]` | MANAGER+ | CRUD user |
| PUT | `/api/admin/users/{id}/role` | MANAGER+ | Đổi role (service có guard) |
| PUT | `/api/admin/users/{id}/status` | MANAGER+ | Đổi trạng thái |
| GET/PUT | `/api/user/profile` | USER | Xem/cập nhật profile từ JWT |
| POST/GET/PUT/DELETE | `/api/user/shipping-addresses[/{id}]` | USER | Địa chỉ giao hàng |

## Field, pricing, holiday, maintenance

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/public/fields` | PUB | Danh sách/filter sân |
| GET | `/api/public/fields/available` | PUB | Tìm sân trống |
| GET | `/api/public/fields/suggestions` | PUB | Gợi ý sân/slot |
| GET | `/api/public/fields/{id}` | PUB | Chi tiết |
| GET | `/api/public/fields/{fieldId}/slots` | PUB | Slot khả dụng |
| POST/PUT/DELETE | `/api/admin/fields[/{id}]` | MANAGER+ | CRUD sân |
| PUT | `/api/admin/fields/{id}/status` | MANAGER+ | Đổi trạng thái |
| POST/GET/PUT/DELETE | `/api/admin/maintenance[/{id}]` | MANAGER+ | Lịch bảo trì |
| GET | `/api/admin/maintenance/field/{fieldId}` | MANAGER+ | Bảo trì theo sân |
| GET | `/api/public/fields/{fieldId}/maintenance-status` | PUB | Trạng thái bảo trì |
| GET/POST/PUT/DELETE | `/api/admin/pricing-rules[/{id}]` | MANAGER+ | Giá thường |
| GET | `/api/admin/pricing-rules/field/{fieldId}` | MANAGER+ | Giá theo sân |
| GET/POST/PUT/DELETE | `/api/admin/holiday-pricing-rules[/{id}]` | MANAGER+ | Giá ngày lễ |
| GET | `/api/public/holidays[/{id}]` | PUB | Đọc ngày lễ |
| POST/PUT/DELETE | `/api/admin/holidays[/{id}]` | MANAGER+ | CRUD ngày lễ |

## Booking, extra, voucher, calendar

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| POST/GET | `/api/user/bookings` | USER | Tạo/list booking của tôi |
| GET | `/api/user/bookings/{bookingId}` | USER | Chi tiết của tôi |
| PATCH | `/api/user/bookings/{bookingId}/cancel` | USER | Hủy |
| GET/POST | `/api/admin/bookings` | STAFF+ | List/tạo booking |
| GET/DELETE | `/api/admin/bookings/{bookingId}` | STAFF+ | Xem/xóa |
| GET | `/api/admin/bookings/{bookingId}/histories` | STAFF+ | Lịch sử |
| PATCH | `/api/admin/bookings/{id}/confirm` | STAFF+ | Xác nhận |
| PATCH | `/api/admin/bookings/{id}/status` | STAFF+ | Chuyển trạng thái |
| PATCH | `/api/admin/bookings/{id}/check-in` | STAFF+ | Check-in |
| PATCH | `/api/admin/bookings/{id}/check-out` | STAFF+ | Check-out |
| GET | `/api/admin/calendar/fields?date=...` | STAFF+ | Calendar theo sân |
| GET | `/api/public/extra-services[/{id}]` | PUB | Dịch vụ thêm |
| GET | `/api/admin/extra-services[/{id}]` | STAFF+ | Đọc quản trị |
| POST/PUT/DELETE | `/api/admin/extra-services[/{id}]` | MANAGER+ | CRUD dịch vụ |
| GET | `/api/public/vouchers` | PUB | Danh sách |
| GET | `/api/public/vouchers/{id}` | PUB | Chi tiết |
| GET | `/api/public/vouchers/code/{code}` | PUB | Tra code |
| GET | `/api/public/vouchers/apply` | PUB | Preview áp voucher |
| POST/PUT/DELETE | `/api/admin/vouchers[/{id}]` | MANAGER+ | CRUD voucher |

## Catalog

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/public/products` | PUB | List/filter |
| GET | `/api/public/products/top4-popular` | PUB | Top sản phẩm |
| GET | `/api/public/products/{id}` | PUB | Chi tiết |
| POST/PUT/DELETE | `/api/admin/products[/{id}]` | MANAGER+ | CRUD |
| GET/POST/PUT/DELETE | `/api/public|admin/brands...` | PUB read/MANAGER+ write | Brand |
| GET/POST/PUT/DELETE | `/api/public|admin/categories...` | PUB read/MANAGER+ write | Category |
| GET/POST/PUT/DELETE | `/api/public|admin/sizes...` | PUB read/MANAGER+ write | Size |
| GET | `/api/public/product-variants[/{id}]` | PUB | Variant |
| POST/PUT/DELETE | `/api/admin/product-variants[/{id}]` | MANAGER+ | CRUD variant |
| POST | `/api/admin/products/{productId}/images` | MANAGER+ | Thêm ảnh |
| GET | `/api/public/products/{productId}/images` | PUB | Ảnh product |
| PUT | `/api/admin/product-images/{imageId}/primary` | MANAGER+ | Chọn primary |
| DELETE | `/api/admin/product-images/{imageId}` | MANAGER+ | Xóa ảnh |

## Cart, order, payment

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/user/carts` | USER | Giỏ của tôi |
| POST | `/api/user/carts/items` | USER | Thêm/cập nhật item |
| DELETE | `/api/user/carts/items/{cartItemId}` | USER | Xóa item |
| POST | `/api/user/orders` | USER | Tạo order |
| POST | `/api/user/orders/checkout-cart` | USER | Checkout item đã chọn |
| GET | `/api/user/orders/me` | USER | Order của tôi |
| GET | `/api/user/orders/{id}` | USER | Chi tiết |
| PUT | `/api/user/orders/{id}/payment-submitted` | USER | Báo chuyển khoản |
| PUT | `/api/user/orders/{id}/confirm-receipt` | USER | Xác nhận nhận |
| PUT | `/api/user/orders/{id}/cancel` | USER | Hủy |
| GET | `/api/admin/orders` | STAFF+ | List admin |
| PUT | `/api/admin/orders/{id}/status` | STAFF+ | Chuyển trạng thái |
| DELETE | `/api/admin/orders/{id}` | STAFF+ | Xóa |
| GET/PUT/DELETE | `/api/public/orderItems/...` | PUB matcher | Đọc/sửa/xóa order item; cần audit |
| GET | `/api/admin/payments` | MANAGER+ | List payment |
| GET | `/api/public/payments/{id}` | PUB | Chi tiết payment |
| GET | `/api/user/payments/orders/{orderId}` | USER | Payment của order |
| PUT | `/api/admin/payments/orders/{orderId}/confirm` | MANAGER+ | Xác nhận |
| DELETE | `/api/admin/payments/{id}` | MANAGER+ | Xóa |
| GET | `/api/user/orders/{orderId}/qr-url` | USER | QR order |
| GET | `/api/user/bookings/{bookingId}/qr-url` | USER | QR booking |

## Review, notification, dashboard

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/public/products/{productId}/reviews` | PUB | Review product |
| GET | `/api/public/products/{productId}/average-rating` | PUB | Rating |
| GET | `/api/user/order-items/{orderItemId}/can-review` | USER | Kiểm tra quyền |
| POST | `/api/user/order-items/{orderItemId}/reviews` | USER | Tạo review |
| DELETE | `/api/admin/reviews/{reviewId}` | MANAGER+ | Xóa review |
| POST | `/api/user/booking-reviews/{bookingId}` | USER | Review booking |
| GET | `/api/public/booking-reviews/field/{fieldId}` | PUB | Review sân |
| GET | `/api/public/booking-reviews/field/{fieldId}/summary` | PUB | Tổng hợp |
| GET | `/api/user/booking-reviews/my-reviews` | USER | Review của tôi |
| GET | `/api/user/notifications` | USER | Danh sách |
| GET | `/api/user/notifications/unread-count` | USER | Số chưa đọc |
| PATCH | `/api/user/notifications/{id}/read` | USER | Đã đọc |
| PATCH | `/api/user/notifications/read-all` | USER | Đọc tất cả |
| GET | `/api/admin/dashboard/summary` | STAFF+ | KPI |
| GET | `/api/admin/dashboard/monthly-revenue` | STAFF+ | Revenue chart |
| GET | `/api/admin/dashboard/admin-overview` | STAFF+ | Overview |
| GET | `/api/user/wishlist` | USER | Wishlist của tôi |
| POST/DELETE | `/api/user/wishlist/{productId}` | USER | Thêm/xóa yêu thích |
| GET | `/api/user/wishlist/{productId}/exists` | USER | Kiểm tra yêu thích |

## Inventory, importer, membership, shared

| Method | Endpoint | Quyền | Mục đích |
|---|---|---|---|
| POST/PUT/GET | `/api/admin/inventory/items[/{id}]` | MANAGER+ | Inventory item |
| GET | `/api/admin/inventory/stocks` | MANAGER+ | Stock |
| GET | `/api/admin/inventory/transactions` | MANAGER+ | Ledger |
| GET | `/api/admin/inventory/reservations` | MANAGER+ | Reservation |
| POST/PUT/GET | `/api/admin/inventory/suppliers[/{id}]` | MANAGER+ | Supplier |
| POST/GET | `/api/admin/inventory/imports` | MANAGER+ | Tạo/list stock import |
| GET | `/api/admin/inventory/imports/{id}` | MANAGER+ | Chi tiết |
| POST | `/api/admin/inventory/imports/{id}/post` | MANAGER+ | Post phiếu |
| POST | `/api/admin/inventory/imports/{id}/cancel` | MANAGER+ | Hủy phiếu |
| GET | `/api/admin/inventory/consistency` | MANAGER+ | Kiểm tra nhất quán |
| POST | `/api/admin/inventory/adjustments` | MANAGER+ | Điều chỉnh |
| GET/POST | `/api/admin/inventory/product-variant-sync[/preview]` | MANAGER+ | Sync variant |
| GET | `/api/admin/imports/templates/{type}` | MANAGER+ | CSV template |
| POST | `/api/admin/imports/{type}` | MANAGER+ | Import CSV |
| GET/POST/PUT/DELETE | `/api/admin/membership-tiers[/{id}]` | ADMIN | Tier |
| POST | `/api/admin/products/{productId}/images` | MANAGER+ | Upload ảnh sản phẩm lên Cloudinary |
# CẬP NHẬT API — 2026-07-01

| Method | Endpoint | Mục đích |
|---|---|---|
| PATCH | `/api/admin/inventory/items/{id}/low-stock-threshold` | Đặt ngưỡng cảnh báo |
| PATCH | `/api/admin/inventory/items/{id}/inventory-behavior` | Đặt hành vi kho |
| GET | `/api/admin/inventory/profit-summary` | Tổng hợp doanh thu, giá vốn, lợi nhuận |
| GET | `/api/admin/products/{id}/publication-check` | Kiểm tra Product có thể publish |
| POST | `/api/admin/products/{id}/publish` | Mở bán Product |
| POST | `/api/admin/products/{id}/unpublish` | Ngừng public Product |
| GET | `/api/admin/extra-services/{id}/publication-check` | Kiểm tra Extra có thể publish |
| POST | `/api/admin/extra-services/{id}/publish` | Mở bán Extra |
| POST | `/api/admin/extra-services/{id}/unpublish` | Ngừng public Extra |

- `/api/public/products` và `/api/public/extra-services` chỉ trả dữ liệu `ACTIVE`.
- API slot public và calendar admin cùng dùng slot 90 phút trong `07:00–23:45`; slot cuối `22:00–23:30`.
- Import type có thêm `CATEGORIES` và `BRANDS`.
# CẬP NHẬT API FLOW — 2026-07-01

| Method | Endpoint | Mục đích |
|---|---|---|
| PATCH | `/api/user/bookings/{id}/payment-submitted` | Khách báo đã chuyển khoản booking |
| PUT | `/api/user/orders/{id}/payment-submitted` | Khách báo đã chuyển khoản order |
| PUT | `/api/admin/payments/bookings/{id}/confirm` | Admin xác nhận tiền booking |
| PUT | `/api/admin/payments/orders/{id}/confirm` | Admin xác nhận tiền order |

- VNPay đã được loại khỏi API; payment hiện hỗ trợ CASH/COD/VietQR theo từng nghiệp vụ.
- DELETE booking/order vẫn giữ URL tương thích UI nhưng semantics là cancel an toàn, không hard-delete.

## ORDER ITEM SAU CHECKOUT

- `GET /api/user/order-items/order/{orderId}` yêu cầu đăng nhập và kiểm tra chủ sở hữu.
- PUT/DELETE từng order item bị từ chối sau checkout; khách phải hủy đơn `PENDING` và đặt lại để reservation, payment và tồn kho không lệch.
