# CLS-00 Layered Overall Class Diagram

## Mục đích

Diagram này là bản class diagram tổng quan ít rối hơn cho hệ thống Football E-commerce + Booking. Bố cục được chia thành ba lớp: `ServiceLayer`, `CoreDomain` và `SupportingDomain` để hạn chế dây chéo khi import vào draw.io hoặc chèn vào Word A4.

## Cách đọc diagram

- `ServiceLayer`: các service điều phối nghiệp vụ chính trong Spring Boot.
- `CoreDomain`: các class/entity lõi của hệ thống như `User`, `Product`, `Order`, `Booking`, `Payment`, `RefundRequest`, `InventoryItem`.
- `SupportingDomain`: các class phụ trợ như biến thể sản phẩm, giỏ hàng, order item, sân bóng, dịch vụ thêm và tồn kho.

## Class chính

- Service: `UserService`, `ProductService`, `OrderService`, `BookingService`, `PaymentService`, `InventoryService`.
- Core domain: `User`, `MembershipTier`, `Product`, `Order`, `Booking`, `Payment`, `RefundRequest`, `InventoryItem`.
- Supporting domain: `ProductVariant`, `Cart`, `OrderItem`, `FootballField`, `BookingExtraService`, `ExtraService`, `InventoryStock`, `InventoryReservation`.

## Quan hệ chính

- `User` tạo `Order`, `Booking` và yêu cầu `RefundRequest`.
- `Product` có nhiều `ProductVariant`; `Order` chứa nhiều `OrderItem`.
- `Booking` gắn với `FootballField` và có nhiều `BookingExtraService`.
- `Order` và `Booking` đều có thể liên kết với `Payment`.
- `Payment` là nguồn phát sinh `RefundRequest`.
- `InventoryItem` quản lý `InventoryStock` và `InventoryReservation`.
- `ProductVariant` và `ExtraService` liên kết mềm tới `InventoryItem`.

## Field/method đã rút gọn

Mỗi class chỉ giữ 2-4 field và 1-3 method tiêu biểu để diagram vừa khổ A4. Method trong entity thể hiện hành vi nghiệp vụ ở mức thiết kế UML; trong source Spring Boot hiện tại, phần lớn logic được triển khai tại service.

## Đoạn đưa vào báo cáo

Class diagram tổng quan được tổ chức theo ba lớp để thể hiện rõ trách nhiệm trong hệ thống. Tầng service chịu trách nhiệm điều phối các nghiệp vụ chính như mua hàng, đặt sân, thanh toán và quản lý kho. Tầng core domain biểu diễn các đối tượng nghiệp vụ cốt lõi như người dùng, sản phẩm, đơn hàng, booking, thanh toán và hoàn tiền. Tầng supporting domain chứa các đối tượng phụ trợ giúp hoàn thiện nghiệp vụ như biến thể sản phẩm, dịch vụ thêm và giữ chỗ tồn kho. Cách bố trí này giúp diagram dễ đọc hơn so với việc nối toàn bộ class trong một mặt phẳng lớn.
