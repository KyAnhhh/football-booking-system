# CLS-SVC-03 Refund, Inventory Service

## Mục đích

Class diagram này mô tả các service và phương thức nghiệp vụ chính của luồng hoàn tiền và quản lý kho.

## Class/service

- `RefundService`
- `InventoryService`
- `ProductVariantInventorySyncService`
- Entity rút gọn: `RefundRequest`, `Payment`, `InventoryItem`, `InventoryStock`, `InventoryTransaction`, `InventoryReservation`, `Supplier`, `StockImport`, `StockImportItem`

## Phương thức nghiệp vụ chính

- Refund: `createForCancellation`, `submitBankAccount`, `reviewPayment`, `startProcessing`, `complete`, `fail`
- Inventory: `createItem`, `updateItem`, `createSupplier`, `importStock`, `postStockImport`, `cancelStockImport`, `adjustStock`
- Stock flow: `reserveStock`, `commitReservation`, `releaseReservation`, `checkConsistency`, `getProfitSummary`
- Sync: `previewSync`, `syncAll`

## Ghi chú rút gọn

Các method dài được giữ ở mức tên nghiệp vụ chính, không liệt kê toàn bộ tham số để tránh diagram bị quá rộng. Các entity chỉ hiển thị field trạng thái, số lượng, giá vốn và định danh giao dịch.

## Đoạn đưa vào báo cáo

Luồng hoàn tiền được quản lý bằng `RefundService`, cho phép tạo yêu cầu khi hủy đơn/booking, thu thập thông tin tài khoản ngân hàng, duyệt bằng chứng và hoàn tất hoặc đánh dấu thất bại. Phần kho được quản lý bởi `InventoryService` với các thao tác tạo item, nhập kho, điều chỉnh, giữ chỗ, xác nhận giữ chỗ và giải phóng giữ chỗ. Cách tách `InventoryItem`, `InventoryStock`, `InventoryTransaction` và `InventoryReservation` giúp hệ thống vừa biết tồn kho hiện tại vừa truy vết được lịch sử biến động. `ProductVariantInventorySyncService` hỗ trợ đồng bộ biến thể sản phẩm với item kho. Sơ đồ này phù hợp để trình bày cho giảng viên thấy hệ thống có xử lý vận hành sau bán hàng, không chỉ CRUD cơ bản.
