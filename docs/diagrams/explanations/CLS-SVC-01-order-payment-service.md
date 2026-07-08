# CLS-SVC-01 Order, Cart, Payment Service

## Mục đích

Class diagram này mô tả các service và phương thức nghiệp vụ chính trong flow mua sản phẩm: giỏ hàng, checkout, tạo đơn, xác nhận thanh toán, hủy đơn và hoàn tồn kho.

## Class/service

- `CartService`
- `OrderService`
- `PaymentService`
- `InventoryService`
- `RefundService`
- Entity rút gọn: `Cart`, `CartItem`, `Order`, `OrderItem`, `Payment`

## Phương thức nghiệp vụ chính

- `addItemToCart`, `removeItemFromCart`, `getCartByUser`
- `checkout`, `createOrder`, `markPaymentSubmitted`, `updateOrderStatus`, `cancelOrder`, `confirmReceive`
- `confirmPayment`, `getPaymentByOrderId`
- `reserveStock`, `commitReservation`, `releaseReservation`, `deductForOrder`, `returnForOrder`
- `createForCancellation`

## Ghi chú rút gọn

Diagram không vẽ repository/controller để tránh quá rộng khi chèn Word A4. Một số tham số dài của inventory được rút gọn bằng dấu `...` nhưng vẫn giữ đúng ý nghĩa nghiệp vụ từ source code.

## Đoạn đưa vào báo cáo

Flow mua sản phẩm được điều phối chủ yếu bởi `CartService`, `OrderService` và `PaymentService`. Người dùng thêm sản phẩm vào giỏ hàng, sau đó `OrderService` thực hiện checkout, tạo đơn và liên kết thanh toán. Tồn kho được kiểm soát thông qua `InventoryService`, gồm giữ chỗ, xác nhận trừ kho hoặc hoàn lại khi hủy đơn. Khi đơn đã phát sinh thanh toán và bị hủy, `RefundService` tạo yêu cầu hoàn tiền để admin xử lý. Sơ đồ này giúp thể hiện rõ trách nhiệm của từng service trong nghiệp vụ bán hàng.
