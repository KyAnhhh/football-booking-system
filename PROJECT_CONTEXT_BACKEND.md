# Project Context — Backend

## Khởi chạy và cấu hình

- Entry point: `ShopbongdaApplication`.
- Mặc định chạy port `8080`, MySQL database `football_booking_system`, Redis `localhost:6379`.
- Security stateless bằng bearer JWT. `/api/auth/**` và phần lớn `/api/public/**` public; dashboard dù mang tên public chỉ dành cho staff/manager/admin.
- `WebConfig` cấu hình CORS; `RedisConfiguration`, `JwtDecoderConfiguration`, `CloudinaryConfig`, `VNPayConfig` cung cấp integration.
- `GlobalExceptionHandler` xử lý exception tập trung nhưng nhiều service vẫn ném `RuntimeException`.
- Resources có migration V2–V19 cho pricing, voucher usage, booking extras, notification, holiday, inventory, reservation, shipping address và stock-import lifecycle.

## Các module

### 1. User, authentication và shipping

- Model: `User`, `Role`, `ShippingAddress`, `PasswordResetToken`, `RedisToken`.
- Repository: `UserRepository`, `ShippingAddressRepository`, `PasswordResetTokenRepository`, `RedisTokenRepository`.
- Service: `AuthenticationService`, `JwtService`, `PasswordResetTokenService`, `UserService/Impl`, `ShippingAddressService/Impl`, `ShippingService`, `UserDetailServiceCustomizer`.
- Controller: `AuthenticationController`, `UserController`, `ShippingAddressController`.
- Luồng: đăng ký mã hóa BCrypt; login xác thực và phát access/refresh token; logout lưu JWT ID vào Redis; refresh rotate token; quên mật khẩu tạo token 15 phút và gửi email; admin quản trị user/role/status; user quản lý profile và địa chỉ.
- Trạng thái: chức năng chính đã nối UI. Có rủi ro hard-code mail/JWT secret, log refresh token ở frontend, profile API đặt dưới `/public` nhưng vẫn dựa vào user xác thực.

### 2. Football field và maintenance

- Model: `FootballField`, `FootballFieldImage`, `FieldType`, `FieldStatus`, `SuggestionType`, `Maintenance`.
- Repository: `FootballFieldRepository`, `FootballFieldImageRepository`, `MaintenanceRepository`.
- Service: `FootballFieldService/Impl`, `MaintenanceService/Impl`.
- Controller: `FootballFieldController`, `MaintenanceController`.
- Luồng: admin CRUD sân và trạng thái; public tìm/filter sân, sân trống, gợi ý, chi tiết, slot; service kiểm tra booking trùng và lịch bảo trì. Admin tạo lịch bảo trì có kiểm tra overlap; public xem maintenance status.
- Trạng thái: đã có UI admin/client. Ảnh sân nằm trong domain/repository nhưng không thấy controller/service CRUD riêng.

### 3. Pricing và holiday

- Model: `FieldPricingRule`, `HolidayPricingRule`, `Pricing`, `Holiday`.
- Repository: `FieldPricingRuleRepository`, `HolidayPricingRuleRepository`, `HolidayRepository`.
- Service: `FieldPricingRuleService/Impl`, `HolidayPricingRuleService/Impl`, `PricingService/Impl`, `HolidayService/Impl`.
- Controller: `FieldPricingRuleController`, `HolidayPricingRuleController`, `AdminHolidayController`, `HolidayController`.
- Luồng: cấu hình giá theo sân/ngày trong tuần/khung giờ; giá ngày lễ override/điều chỉnh; `PricingService` sinh slot và tính tổng giá. Admin CRUD holiday/rules; public đọc holiday.
- Trạng thái: đã nối các trang admin và được booking dùng.

### 4. Booking và lịch sân

- Model: `Booking`, `BookingStatus`, `BookingStatusHistory`.
- Repository: `BookingRepository`, `BookingStatusHistoryRepository`.
- Service: `BookingService/Impl`, `BookingStatusHistoryService/Impl`, `AdminFieldCalendarService/Impl`.
- Controller: `UserBookingController`, `AdminBookingController`, `AdminFieldCalendarController`.
- Liên quan: `FootballField`, pricing, maintenance, extra service, voucher usage, payment, inventory reservation, notification/email.
- Luồng chính:
  1. Client chọn sân, ngày/slot, dịch vụ thêm và voucher.
  2. Service kiểm tra slot/maintenance, tính giá từng slot, áp voucher, giữ/khấu trừ tài nguyên tồn kho phù hợp rồi tạo booking và lịch sử.
  3. Admin có thể tạo hộ khách, confirm, đổi trạng thái, check-in/out hoặc xóa.
  4. Mỗi chuyển trạng thái ghi history, gửi notification/email và cập nhật voucher/inventory theo nhánh hủy/hoàn tất.
  5. Calendar tổng hợp booking theo sân/ngày cho staff.
- Trạng thái: là module lớn và đã có UI hai phía. Cần integration test cho state machine, concurrency slot và rollback chéo inventory/voucher.

### 5. Extra service

- Model: `ExtraService`, `BookingExtraService`.
- Repository: `ExtraServiceRepository`, `BookingExtraServiceRepository`.
- Service: `ExtraServiceService/Impl`; mapper `ExtraServiceMapper`.
- Controller: `PublicExtraServiceController`, `AdminExtraServiceController`.
- Luồng: admin CRUD dịch vụ; client lấy danh sách và gắn số lượng vào booking; có thể liên kết `InventoryItem` để kiểm soát tồn.
- Trạng thái: đã nối UI. Cần kiểm tra migration/data cũ khi dịch vụ chuyển sang stock-backed.

### 6. Voucher

- Model: `Voucher`, `VoucherUsage`, `DiscountType`.
- Repository: `VoucherRepository`, `VoucherUsageRepository`.
- Service: `VoucherService/Impl`, `VoucherApplicationResult`.
- Controller: `VoucherController`, `AdminVoucherController`.
- Luồng: admin CRUD; public tra cứu/preview theo code và amount; booking ghi usage, giới hạn tổng/per-user và xử lý refund khi hủy.
- Trạng thái: đã nối UI và booking. Nhiều validation đang dùng `RuntimeException`.

### 7. Payment và QR

- Model: `Payment`, `PaymentMethod`, `PaymentStatus`, `PaymentType`.
- Repository: `PaymentRepository`.
- Service: `PaymentService/Impl`, `VNPayService`, `QrCodeService`.
- Controller: `PaymentController`, `QrCodeController`.
- Luồng: booking tạo VNPay URL; order dùng payment record/VietQR và admin xác nhận; user lấy QR URL sau khi controller kiểm tra ownership.
- Trạng thái: VNPay cấu hình sandbox (`DEMO`); callback/return URL được cấu hình nhưng controller hiện không liệt kê endpoint `vnpay-return`, cần xác minh end-to-end.

### 8. Product catalog

- Model: `Brand`, `Category`, `Product`, `ProductImage`, `ProductVariant`, `Size`.
- Repository: repository tương ứng cho cả sáu model.
- Service/mapper: `Brand`, `Category`, `Product`, `ProductImage`, `ProductVariant`, `Size` service + impl + mapper.
- Controller: `BrandController`, `CategoryController`, `ProductController`, `ProductImageController`, `ProductVariantController`, `SizeController`.
- Luồng: public browse/filter/detail/top popular; admin CRUD catalog; upload URL ảnh và chọn ảnh primary; variant kết hợp product/size và liên kết inventory.
- Trạng thái: catalog client/admin cơ bản hoàn thành. `ProductVariant.js` và `ImagePage.js` tồn tại nhưng chưa có route admin.

### 9. Cart, order và shipping fee

- Model: `Cart`, `CartItem`, `Order`, `OrderItem`, `OrderStatus`.
- Repository: `CartRepository`, `CartItemRepository`, `OrderRepository`, `OrderItemRepository`.
- Service: `CartService/Impl`, `OrderService/Impl`, `OrderItemService/Impl`; `ShippingService`.
- Controller: `CartController`, `UserOrderController`, `AdminOrderController`, `OrderItemController`.
- Luồng: client thêm/xóa variant trong cart, chọn item checkout, địa chỉ và phương thức thanh toán; order tạo snapshot item, phí ship, payment và inventory reservation/transaction; client báo đã chuyển khoản, xác nhận nhận hàng hoặc hủy; admin chuyển trạng thái và gửi notification.
- Trạng thái: đã nối UI. `OrderItemController` nằm dưới `/api/public/orderItems` và cho sửa/xóa item; cần rà soát authorization vì namespace public.

### 10. Inventory và stock import

- Model: `InventoryItem`, `InventoryItemType`, `InventoryStock`, `InventoryTransaction`, `InventoryTransactionType`, `InventoryReferenceType`, `InventoryReservation`, `InventoryReservationStatus`, `Supplier`, `StockImport`, `StockImportItem`, `StockImportStatus`.
- Repository: `InventoryItemRepository`, `InventoryStockRepository`, `InventoryTransactionRepository`, `InventoryReservationRepository`, `SupplierRepository`, `StockImportRepository`.
- Service: `InventoryService/Impl`, `ProductVariantInventorySyncService/Impl`; worker `ProductVariantInventorySyncWorker`.
- Controller: `AdminInventoryController`.
- Luồng: CRUD item/supplier; theo dõi stock, transaction và reservation; tạo phiếu nhập dạng draft, post để tăng kho, cancel; adjustment; consistency check; preview/sync variant sang inventory. Booking/order/extra service tham chiếu kho.
- Trạng thái: feature phong phú và có UI, nhưng đang có thay đổi local chưa commit liên quan lifecycle/consistency V19. Đây là khu vực cần test trước khi coi là ổn định.

### 11. CSV importer

- Model: enum `ImportType`.
- Service: `ImportService/Impl`; DTO `ImportResultResponse`, `ImportRowIssueResponse`.
- Controller: `AdminImportController`.
- Luồng: tải template theo loại; upload CSV multipart; parse/validate từng dòng và trả lỗi theo row. Hỗ trợ nhiều master-data/domain type.
- Trạng thái: có UI admin. Nhiều `return null` trong parser là sentinel cho ô trống/parse thất bại chứ không đủ để kết luận stub; cần test từng import type.

### 12. Review

- Model: `Review` (sản phẩm), `BookingReview` (sân), `WishList`.
- Repository: `ReviewRepository`, `BookingReviewRepository`.
- Service: `ReviewService/Impl`, `BookingReviewService/Impl`.
- Controller: `ReviewController`, `BookingReviewController`.
- Luồng: chỉ order đã nhận mới review product/order item; chỉ booking completed và thuộc user mới review sân; public xem rating/list; admin xóa product review.
- Trạng thái: review đã nối UI. `WishList` chỉ có entity, chưa có API hay UI.

### 13. Notification

- Model: `Notification`, `NotificationType`.
- Repository: `NotificationRepository`.
- Service: `NotificationService/Impl`.
- Controller: `NotificationController`.
- Luồng: booking/order tạo notification cho admin hoặc user; client lấy danh sách/unread count và mark read/all.
- Trạng thái: đã có `NotificationBell`. Không có realtime/SSE/WebSocket; UI phải polling/refetch.

### 14. Dashboard

- Model: DTO `DashboardSummaryResponse`, `RevenueChartResponse`, `AdminOverviewResponse`.
- Service: `DashboardService/Impl`.
- Controller: `DashboardController`.
- Luồng: aggregate revenue/order/booking/user và chuỗi doanh thu tháng.
- Trạng thái: đã nối dashboard/Recharts. Namespace `/public/dashboard` gây nhầm nhưng security giới hạn staff+.

### 15. Membership

- Model: `MembershipTier`; user có quan hệ/điểm thành viên qua `MembershipService`.
- Repository: `MembershipTierRepository`.
- Service: `MembershipTierService/Impl`, `MembershipService/Impl`.
- Controller: `MembershipTierController`.
- Luồng: admin CRUD tier; membership service cập nhật/xếp tier theo chi tiêu.
- Trạng thái: backend hiện hữu nhưng không có frontend service/page/route; chỉ ADMIN được phép.

### 16. Shared và phần trống

- Shared: `ApiResponse`, `EmailService`, `ImageService`/`ImageController`, `QrCodeService`/controller.
- Upload ảnh dùng Cloudinary; endpoint nằm dưới `/api/public/images/upload`, vì vậy hiện public có quyền upload.
- `combo/` đang trống.
- `exception/` có `GlobalExceptionHandler`, `AuthException`, `UserNotFoundException`, `ProductNotFoundException`.

## Testing

- Chỉ thấy `ShopbongdaApplicationTests` (context load); không có unit/service/repository/controller/integration tests theo module.
- Không thấy test concurrency cho double-booking, inventory reservation, voucher limit hay state transitions.

