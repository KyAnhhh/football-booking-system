# Project Context — Frontend

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
- Chưa có route cho product variants/images; membership thiếu hoàn toàn phía UI.
- `refresh()` trong `authService` có `console.log` refresh token và `console.log(refresh.status)` (tham chiếu chính function), cần sửa.
- Không thấy error boundary, query/cache library hoặc test nghiệp vụ.
- CRA test hiện chủ yếu là file mặc định `App.test.js`; có khả năng không còn khớp UI hiện tại.

