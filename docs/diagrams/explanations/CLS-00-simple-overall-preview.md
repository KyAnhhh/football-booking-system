# CLS-00 Simple Overall Class Diagram Preview

## Mục đích

File này là bản class diagram tổng theo phong cách UML cho toàn bộ hệ thống. Diagram không chỉ liệt kê entity như bảng dữ liệu, mà còn thêm method nghiệp vụ trong entity ở mức thiết kế và các service class có phương thức xử lý chính.

## Danh sách entity/domain class chính

### User/Auth/Membership

- `User`
- `MembershipTier`
- `ShippingAddress`
- `PasswordResetToken`
- `Notification`

### Product/E-commerce

- `Product`
- `Category`
- `Brand`
- `Size`
- `ProductVariant`
- `ProductImage`
- `Cart`
- `CartItem`
- `Order`
- `OrderItem`
- `WishlistItem`
- `Review`

### Booking/Field/Pricing

- `FootballField`
- `FootballFieldImage`
- `Booking`
- `BookingExtraService`
- `ExtraService`
- `Maintenance`
- `FieldPricingRule`
- `Holiday`
- `HolidayPricingRule`
- `Voucher`
- `VoucherUsage`
- `BookingReview`
- `BookingStatusHistory`

### Payment/Refund

- `Payment`
- `RefundRequest`

### Inventory

- `InventoryItem`
- `InventoryStock`
- `InventoryTransaction`
- `InventoryReservation`
- `Supplier`
- `StockImport`
- `StockImportItem`

Trong bản tổng A4, một số entity phụ như `Category`, `Brand`, `Size`, `ProductImage`, `FootballFieldImage`, `Maintenance`, `FieldPricingRule`, `Holiday`, `HolidayPricingRule`, `VoucherUsage`, `BookingReview`, `BookingStatusHistory`, `InventoryTransaction`, `StockImportItem` đã được lược bớt khỏi hình tổng để tránh quá rối. Các entity này vẫn có trong ERD chi tiết và các class diagram module.

## Service class và phương thức chính

- `UserService`: quản lý hồ sơ, vai trò và trạng thái người dùng.
- `ProductService`: tạo, cập nhật, xóa và xem sản phẩm.
- `CartService`: xem giỏ hàng, thêm/xóa sản phẩm khỏi giỏ.
- `OrderService`: checkout, gửi minh chứng thanh toán, cập nhật trạng thái, hủy đơn, xác nhận nhận hàng.
- `BookingService`: tạo booking, xem slot trống, gửi minh chứng, check-in/check-out, hủy booking.
- `PaymentService`: xác nhận thanh toán order/booking.
- `RefundService`: tạo, duyệt, hoàn tất hoặc đánh dấu thất bại yêu cầu hoàn tiền.
- `InventoryService`: quản lý item kho, nhập kho, giữ chỗ, xác nhận/giải phóng giữ chỗ và điều chỉnh kho.

## Quan hệ chính

- `User` liên kết với địa chỉ giao hàng, giỏ hàng, đơn hàng, booking, review, wishlist, thông báo và refund.
- `Product` liên kết với danh mục, thương hiệu, biến thể, ảnh, wishlist và review.
- `Order` chứa nhiều `OrderItem` và có thể liên kết với `Payment`.
- `Booking` liên kết với sân bóng, payment, dịch vụ thêm, voucher usage, review và lịch sử trạng thái.
- `InventoryItem` là lớp trung tâm của kho, liên kết với stock, transaction, reservation và dòng nhập kho.
- `ProductVariant` và `ExtraService` liên kết mềm tới `InventoryItem` qua `inventoryItemId`.

## Field và method đã rút gọn

Mỗi entity chỉ giữ field quan trọng như `id`, tên/mã, trạng thái, giá, số lượng và các field nghiệp vụ chính. Entity cũng có một số method ngắn như `cancel()`, `confirm()`, `calculateTotal()`, `reserveStock()` để diagram giống UML class diagram hơn. Trong source Spring Boot hiện tại, phần lớn logic này được triển khai ở service, vì vậy các method trong entity được hiểu là hành vi nghiệp vụ ở mức thiết kế. Service chỉ giữ các method nghiệp vụ tiêu biểu, không liệt kê toàn bộ overload/tham số/DTO để diagram vừa khổ A4. Không vẽ getter/setter vì source dùng Lombok và các method này không phải nghiệp vụ.

## Đoạn đưa vào báo cáo

Class diagram tổng quan thể hiện các entity và service nghiệp vụ chính của hệ thống Football E-commerce + Booking. Các entity biểu diễn dữ liệu cốt lõi như người dùng, sản phẩm, đơn hàng, booking, thanh toán, hoàn tiền và kho, đồng thời thể hiện các hành vi nghiệp vụ tiêu biểu như hủy đơn, xác nhận thanh toán, đặt sân và giữ chỗ tồn kho. Các service class mô tả nơi điều phối nghiệp vụ trong source code Spring Boot. Quan hệ composition, aggregation và dependency được dùng để phân biệt quan hệ sở hữu dữ liệu và quan hệ sử dụng trong xử lý nghiệp vụ. Bản này phù hợp đặt ở phần thiết kế hướng đối tượng tổng quan, còn ERD chi tiết được dùng cho phần thiết kế cơ sở dữ liệu.
