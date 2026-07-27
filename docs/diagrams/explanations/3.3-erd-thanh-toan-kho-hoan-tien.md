# ERD-04 Payment, Inventory, Refund

## Mục đích

Sơ đồ này mô tả cụm thanh toán, hoàn tiền, quản lý kho, nhà cung cấp và nhập kho.

## Bảng/entity

- `PAYMENT`
- `REFUND_REQUEST`
- `INVENTORY_ITEM`
- `INVENTORY_STOCK`
- `INVENTORY_TRANSACTION`
- `INVENTORY_RESERVATION`
- `SUPPLIER`
- `STOCK_IMPORT`
- `STOCK_IMPORT_ITEM`
- Các entity rút gọn để nối quan hệ: `USER`, `ORDERS`, `BOOKING`, `PRODUCT_VARIANT`, `EXTRA_SERVICE`, `ORDER_ITEM`, `BOOKING_EXTRA_SERVICE`.

## Quan hệ chính

- `PAYMENT` liên kết với order hoặc booking tùy loại giao dịch và có thể phát sinh nhiều `REFUND_REQUEST`.
- `REFUND_REQUEST` lưu user yêu cầu và admin/staff xử lý.
- `INVENTORY_ITEM` có một `INVENTORY_STOCK`, nhiều `INVENTORY_TRANSACTION` và nhiều `INVENTORY_RESERVATION`.
- `SUPPLIER` cung cấp nhiều `STOCK_IMPORT`; mỗi phiếu nhập có nhiều `STOCK_IMPORT_ITEM`.
- `PRODUCT_VARIANT` và `EXTRA_SERVICE` liên kết mềm với `INVENTORY_ITEM` thông qua `inventoryItemId`.

## Field đã rút gọn

Sơ đồ giữ các field quan trọng như trạng thái thanh toán, trạng thái refund, số tiền, tài khoản ngân hàng, mã giao dịch hoàn tiền, tồn kho hiện tại, tồn kho giữ chỗ, phiếu nhập và transaction kho. Quan hệ mềm dựa trên `inventoryItemId`, `referenceId`, `referenceLineId` được thể hiện bằng đường nét đứt vì source code lưu bằng id tham chiếu thay vì object JPA trực tiếp.

## Đoạn đưa vào báo cáo

Cụm payment và inventory hỗ trợ hai nghiệp vụ quan trọng: xác nhận thanh toán và kiểm soát tồn kho. Khi người dùng hủy order hoặc booking đã thanh toán, hệ thống tạo `REFUND_REQUEST` để admin tiếp nhận và cập nhật trạng thái hoàn tiền. Kho được thiết kế theo mô hình item, stock, transaction và reservation, giúp tách rõ số lượng hiện có, số lượng đã giữ và lịch sử biến động. Phiếu nhập kho liên kết với nhà cung cấp và các dòng nhập để tính giá vốn. Một số liên kết tới sản phẩm hoặc dịch vụ thêm được lưu bằng id tham chiếu, phù hợp với cách source code quản lý inventory chung cho nhiều loại đối tượng.
