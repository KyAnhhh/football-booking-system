# ERD-00 Overall System ERD

## Mục đích

Sơ đồ này là ERD tổng quan của toàn bộ hệ thống Football E-commerce + Booking System. Diagram dùng để đặt ở phần thiết kế cơ sở dữ liệu hoặc phần tổng quan kiến trúc dữ liệu trong báo cáo Word.

## Phạm vi entity

Sơ đồ bao gồm các nhóm chính:

- User/Auth/Membership: `USER`, `MEMBERSHIP_TIER`, `SHIPPING_ADDRESS`, `PASSWORD_RESET_TOKEN`, `NOTIFICATION`.
- E-commerce: `CATEGORY`, `BRAND`, `PRODUCT`, `SIZE`, `PRODUCT_VARIANT`, `PRODUCT_IMAGE`, `CART`, `CART_ITEM`, `ORDERS`, `ORDER_ITEM`, `WISHLIST_ITEM`, `REVIEW`.
- Booking/Field/Pricing: `FOOTBALL_FIELD`, `FOOTBALL_FIELD_IMAGE`, `BOOKING`, `BOOKING_EXTRA_SERVICE`, `EXTRA_SERVICE`, `MAINTENANCE`, `FIELD_PRICING_RULE`, `HOLIDAY`, `HOLIDAY_PRICING_RULE`, `BOOKING_REVIEW`, `BOOKING_STATUS_HISTORY`.
- Voucher/Payment/Refund: `VOUCHER`, `VOUCHER_USAGE`, `PAYMENT`, `REFUND_REQUEST`.
- Inventory: `INVENTORY_ITEM`, `INVENTORY_STOCK`, `INVENTORY_TRANSACTION`, `INVENTORY_RESERVATION`, `SUPPLIER`, `STOCK_IMPORT`, `STOCK_IMPORT_ITEM`.

## Quan hệ chính

- `USER` là trung tâm của các nghiệp vụ: đặt hàng, đặt sân, review, wishlist, thông báo và refund.
- `PRODUCT` được tổ chức theo `CATEGORY`, `BRAND`, `SIZE`, `PRODUCT_VARIANT` và `PRODUCT_IMAGE`.
- `ORDERS` chứa nhiều `ORDER_ITEM` và liên kết với `PAYMENT`.
- `BOOKING` liên kết với `FOOTBALL_FIELD`, `PAYMENT`, `BOOKING_EXTRA_SERVICE`, `VOUCHER_USAGE`, `BOOKING_REVIEW` và `BOOKING_STATUS_HISTORY`.
- `INVENTORY_ITEM` là lõi của kho, liên kết với stock, transaction, reservation và stock import.
- `PRODUCT_VARIANT` và `EXTRA_SERVICE` liên kết mềm tới `INVENTORY_ITEM` thông qua `inventoryItemId`.

## Field đã rút gọn

Đây là ERD tổng quan nên mỗi bảng chỉ giữ field tối thiểu: `id`, tên/mã, trạng thái, giá/tổng tiền, số lượng và một số field nghiệp vụ nhận diện. Các field chi tiết hơn được trình bày trong các ERD cụm `ERD-01` đến `ERD-04`.

## Lưu ý khi đưa vào Word

Nên export diagram này ở kích thước lớn, ví dụ 2400x1350 px hoặc SVG, sau đó chèn vào Word với chiều rộng khoảng 15.5 cm. Nếu chữ vẫn nhỏ, dùng diagram này cho phần tổng quan và dùng bốn ERD cụm để trình bày chi tiết.

## Đoạn đưa vào báo cáo

ERD tổng quan thể hiện toàn bộ dữ liệu chính của hệ thống, bao gồm người dùng, sản phẩm, đơn hàng, đặt sân, thanh toán, hoàn tiền và quản lý kho. `USER` đóng vai trò trung tâm vì liên quan đến cả mua hàng, booking, đánh giá, thông báo và yêu cầu hoàn tiền. Phần e-commerce quản lý sản phẩm theo danh mục, thương hiệu, size và biến thể để phục vụ giỏ hàng và đơn hàng. Phần booking quản lý sân bóng, khung giờ đặt, dịch vụ thêm, rule giá, voucher và lịch sử trạng thái. Phần inventory được tách thành item, stock, transaction và reservation để kiểm soát tồn kho và truy vết biến động trong quá trình bán hàng hoặc cung cấp dịch vụ.
