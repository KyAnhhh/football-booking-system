# Project Context — Frontend

> Cập nhật gần nhất: 2026-07-01. Đọc phần **CẬP NHẬT FRONTEND** ở cuối file trước.

## Bootstrap và hạ tầng

- `src/index.js` mount `App`; `App.js` nạp router và global CSS.
- `AppRoutes` ghép auth routes, `AdminRoutes`, `ClientRoutes`.
- `ClientLayout` dùng `Header`/`Footer`; `AdminLayout` dùng outlet/navigation.
- `ProtectedRoute` đọc JWT từ `sessionStorage`, decode claim `role`, chuẩn hóa tiền tố `ROLE_`.
- `services/apiService.js` cung cấp `fetchWithAuth`: thêm bearer token, gom các request gặp 401 trong lúc refresh, rotate token rồi retry; nếu thất bại xóa session và chuyển `/login`.
- `contexts/CartContext.js` quản lý state/count giỏ hàng dùng chung.

## Routes và pages

### Auth

- `/login` → `Login.js`: gọi `authService.login`, lưu access/refresh token, điều hướng theo role.
- `/register` → `Register.js`.
- `/forgot-password` → `Forgot-Password.js`.
- `/reset-password` → `Reset-Password.js`.

### Client

- `/` → `Home`: sân khả dụng và top-4 sản phẩm.
- `/products` → `Product`: filter/paginate catalog bằng product/brand/category services.
- `/products/product-details/:id` → `ProductDetail`: chi tiết, variant, thêm cart, review.
- `/cart` → `Cart`; `/cart/checkout` → `Checkout`: cart → địa chỉ → order → QR/payment.
- `/my-orders` → `MyOrders` + `OrderCard`.
- `/orders/:orderId/review/:productId` → `ReviewSection`.
- `/review/:id` → `Review`: kiểm tra quyền review order item và submit.
- `/football-fields` → `FootballField`: danh sách, availability, suggestion, maintenance.
- `/football-fields/:id/detail` → `FootballFieldDetail`: slot, review, extra service, voucher và tạo booking.
- `/football-fields/:id/payment` → `PaymentFootballField`: tìm booking và hiển thị QR.
- `/my-bookings` → `MyBookings`; `/my-bookings/:bookingId` → `BookingDetailPage`: xem/hủy/review.
- `/profile` → `Profile`; các trang tĩnh: `/about`, `/contact`, `/collections`, `/service`.

Lưu ý: client routes (kể cả profile/cart/my-orders/my-bookings) không bọc `ProtectedRoute`; API vẫn chặn bằng JWT và page/service tự xử lý lỗi. Điều này cho phép mở route nhưng chưa chắc tải được dữ liệu.

### Admin/staff

Toàn bộ `/admin/*` yêu cầu `ADMIN|MANAGER|STAFF`, rồi từng route giới hạn thêm:

- Dashboard, orders, bookings, booking detail, field calendar: staff/manager/admin.
- Users, products, brands, categories, sizes, payments, fields, maintenance, pricing, holiday pricing, vouchers, extra services, holidays, inventory, imports: manager/admin (payments khớp backend).
- Pages: `DashboardPage`, `UserPage`, `ProductPage`, `BrandPage`, `CategoryPage`, `OrderPage`, `SizePage`, `PaymentPage`, `FootballFieldPage`, `AdminFieldCalendarPage`, `BookingPage`, `AdminBookingDetailPage`, `MaintenanceManagementPage`, `PricingRulePage`, `HolidayPricingRulePage`, `VoucherPage`, `ExtraServicePage`, `HolidayPage`, `InventoryPage`, `ImportPage`.

File page chưa được route:

- `pages/admin/ProductVariant.js`
- `pages/admin/ImagePage.js`

Không có page cho membership tiers.

## Service map

- Auth/common: `apiService`, `authService`, `userService`, `shippingAddressService`, `notificationService`.
- Booking field: `fieldService`, `bookingService`, `adminCalendarService`, `extraService`, `holidayService`, `pricingRuleService`, `holidayPricingRuleService`, `voucherService`, `bookingReviewService`, `qrCodeService`.
- Commerce: `brandService`, `categoryService`, `productService`, `productImageService`, `productVariantService`, `sizeService`, `cartService`, `orderService`, `paymentService`, `reviewService`.
- Operations: `dashboardService`, `inventoryService`, `importService`.

Tất cả service dùng URL tuyệt đối `http://localhost:8080`; không thấy `.env`/central API base URL.

## Components

- Client shell: `components/client/Header`, `Footer`.
- Notification: `NotificationBell`.
- Reviews: `ReviewSection`, `ReviewItem`, `ReviewForm`, `RatingSumary` (tên file typo “Sumary”).
- Orders: `OrderCard`.
- Dashboard: `KPICard`, `RevenueChart`; `admin/StatCard`.
- Styling nằm trong `assets/styles` và một số CSS cạnh component.

## Luồng UI chính

### Đặt sân

`FootballField` → chọn sân → `FootballFieldDetail` lấy slot/review/extra → preview voucher → `bookingService.createBooking` → danh sách/chi tiết booking → QR/payment → admin confirm/check-in/check-out/cancel/status; calendar phản ánh lịch.

### Mua hàng

`Product` → `ProductDetail` chọn variant → `CartContext/cartService` → `Checkout` chọn cart item + shipping address + payment → `orderService.checkout` → QR nếu cần → `MyOrders` → xác nhận nhận hàng/review.

### Quản trị

Các page admin dùng service theo module để CRUD và render table/form. Dashboard aggregate KPI; inventory có tab/luồng item-stock-transaction-reservation-supplier-import-consistency/sync; CSV import có template và kết quả theo dòng.

## Trạng thái frontend

- Phần lớn backend API quan trọng đã có service và page tương ứng.
- Product variants/images được quản lý từ `ProductPage`; Membership Tier có route ADMIN; Wishlist có route client được bảo vệ.
- `refresh()` trong `authService` có `console.log` refresh token và `console.log(refresh.status)` (tham chiếu chính function), cần sửa.
- Không thấy error boundary, query/cache library hoặc test nghiệp vụ.
- CRA test hiện chủ yếu là file mặc định `App.test.js`; có khả năng không còn khớp UI hiện tại.
# CẬP NHẬT FRONTEND — 2026-07-01

- `InventoryPage` có tìm kiếm/lọc item, sửa ngưỡng cảnh báo, hành vi kho, giá vốn bình quân, giá trị tồn và tổng hợp lợi nhuận.
- `inventoryService.js` có API profit summary, threshold, behavior, consistency, stock-import lifecycle và ProductVariant sync.
- `ProductPage` và `ExtraServicePage` hỗ trợ publication check/publish/unpublish.
- Public chỉ nhận Product/ExtraService có trạng thái `ACTIVE`.
- `ImportPage`/`importService.js` hỗ trợ Category và Brand cùng các loại import catalog/kho.
- Luồng đặt sân dùng slot backend; slot cuối là `22:00–23:30`.
- `AdminFieldCalendarPage` chỉ render API calendar; lỗi hiển thị `23:30–23:45` đã được sửa tại backend.
- Frontend production build đã compile thành công.
# CẬP NHẬT UI PAYMENT — 2026-07-01

- VNPay đã được loại khỏi frontend; checkout chỉ hiển thị phương thức được backend hỗ trợ.
- Checkout chỉ hiện COD/VietQR; booking chỉ hiện CASH/VietQR.
- Trang thanh toán booking gọi API `payment-submitted` thay vì chỉ hiển thị thông báo.
- Admin Booking Detail có nút xác nhận tiền cho VietQR đang `PENDING`.
- Admin Payment Page có nút xác nhận VietQR của order và không còn nút sửa/xóa payment.
- Frontend production build đã thành công sau thay đổi.

## AUDIT FLOW BOOKING/ORDER — 2026-07-01

- UI admin chỉ hiển thị transition mà backend cho phép; VietQR phải xác nhận payment trước khi xác nhận booking/order.
- Booking `IN_PROGRESS` chỉ được check-out sang `COMPLETED`, không còn thao tác hủy.
- Client có thể mở lại `/orders/:id/payment` để thanh toán đơn VietQR `UNPAID`, gửi yêu cầu xác nhận hoặc hủy đơn `PENDING`.
- Trang payment booking chỉ tạo QR cho `PENDING + VIETQR`; payment `PENDING` hiển thị chờ admin và không gửi lặp.
- Timeline booking, order card và lịch sân đã nhận biết `IN_PROGRESS/SHIPPING`.
- RevenueChart dùng stacked bar (đặt sân/sản phẩm) kết hợp đường tổng doanh thu, tooltip chi tiết, bộ lọc 6/12 tháng/toàn bộ, tổng kỳ và tăng trưởng tháng gần nhất.
- Maintenance form giữ nguyên giờ local khi edit, mặc định lịch mới ở mốc 15 phút tiếp theo trong 2 giờ và chặn start quá khứ/end không hợp lệ trước API.
