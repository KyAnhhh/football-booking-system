# Project Context — Football Booking System

> Cập nhật gần nhất: 2026-07-01. Đọc phần **CẬP NHẬT ƯU TIÊN** ở cuối file trước; phần đó thay thế các trạng thái cũ nếu có khác biệt.

## Mục đích

Đây là bản đồ đọc nhanh của repository tại thời điểm 2026-06-29. Source được chia thành:

- `back-end/`: Spring Boot REST API, persistence, security và nghiệp vụ.
- `front-end/`: React SPA cho khách hàng và nhân sự quản trị.
- `docs/`: PlantUML và ảnh cho use case, class, sequence, activity diagram.
- `IMPLEMENTATION_SUMMARY.md`, `FIX_SUMMARY.md`: ghi chú triển khai trước đây, chỉ dùng tham khảo; context này ưu tiên code hiện tại.

Đọc tiếp:

- [PROJECT_CONTEXT_BACKEND.md](PROJECT_CONTEXT_BACKEND.md): module, entity, repository, service, controller và luồng backend.
- [PROJECT_CONTEXT_FRONTEND.md](PROJECT_CONTEXT_FRONTEND.md): route, page, component, service và luồng giao diện.
- [PROJECT_CONTEXT_API.md](PROJECT_CONTEXT_API.md): danh mục endpoint và quyền truy cập.
- [NEXT_STEPS.md](NEXT_STEPS.md): rủi ro, phần chưa hoàn thiện và việc cần kiểm tra.

## Tech stack

### Backend

- Java 21, Spring Boot 3.5.6, Maven.
- Spring Web, Data JPA/Hibernate, Validation, Security, OAuth2 Resource Server.
- MySQL; Redis dùng lưu JWT đã thu hồi.
- JWT HS512 (Nimbus trong code; dependency JJWT cũng tồn tại).
- Lombok, MapStruct 1.5.5.
- Cloudinary upload ảnh, ZXing tạo QR, Spring Mail.
- Apache Commons CSV cho import.
- SQL migrations nằm tại `src/main/resources/db/migration`, nhưng `application.yaml` không khai báo dependency Flyway và đang để `ddl-auto: update`.
- VNPay sandbox cho thanh toán booking; VietQR/QR cho đơn hàng.

### Frontend

- React 19.1.1 (Create React App/react-scripts 5), JavaScript.
- React Router DOM 7, Bootstrap/React Bootstrap.
- Framer Motion, Lucide React, Recharts, SweetAlert.
- Native `fetch`; wrapper `fetchWithAuth` tự refresh JWT và retry request.
- CSS theo page/component; state giỏ hàng bằng React Context.

## Cấu trúc quan trọng

```text
.
├─ back-end/
│  ├─ pom.xml
│  ├─ docker-compose.yaml
│  ├─ API_DOCUMENTATION.md
│  └─ src/
│     ├─ main/java/vn/phankyanh/shopbongda/
│     │  ├─ booking, calendar, dashboard, extra, field, holiday
│     │  ├─ importer, inventory, maintenance, membership, notification
│     │  ├─ order, payment, pricing, product, review, user, voucher
│     │  └─ config, security, shared, exception, combo
│     ├─ main/resources/
│     │  ├─ application.yaml
│     │  ├─ db/migration (V2–V19)
│     │  ├─ db/demo
│     │  └─ static/logo.jpg
│     └─ test/java/.../ShopbongdaApplicationTests.java
├─ front-end/
│  ├─ package.json
│  ├─ public/
│  └─ src/
│     ├─ components/{admin,client,dashboard,notification,order,review}
│     ├─ contexts/CartContext.js
│     ├─ layouts/{AdminLayout,ClientLayout}.js
│     ├─ pages/{admin,client} + auth pages
│     ├─ routes/{AppRoutes,AdminRoutes,ClientRoutes,ProtectedRoute}.js
│     ├─ services/*.js
│     └─ assets/styles/*.css
└─ docs/{diagrams,images}
```

## Kiến trúc và quy ước

- Backend chủ yếu tổ chức theo feature/domain; mỗi module thường có `domain`, `dto`, `repository`, `service`, `mapper`, `controller`.
- API chia namespace `/api/public`, `/api/user`, `/api/admin`, `/api/auth`.
- Response nhiều endpoint được bọc bằng `ApiResponse`; một số controller trả DTO/`ResponseEntity` trực tiếp.
- Frontend hard-code API origin `http://localhost:8080` trong từng service.
- Access token và refresh token lưu trong `sessionStorage`; route admin kiểm tra claim `role`.
- Vai trò: `CLIENT`, `STAFF`, `MANAGER`, `ADMIN`.

## Trạng thái tổng quan

- Luồng cốt lõi đặt sân, quản lý booking, sản phẩm/giỏ hàng/đơn hàng, kho, voucher, giá, đánh giá và thông báo đã có cả backend và frontend.
- Một số module chỉ có backend hoặc chưa được nối route UI: membership tier; trang `ImagePage.js` và `ProductVariant.js` tồn tại nhưng không được khai báo trong `AdminRoutes`.
- Thư mục backend `combo` đang trống; entity `WishList` chưa có repository/service/controller.
- Test tự động hiện chỉ có một context-load test và test mặc định CRA.
- Module inventory đang có thay đổi local chưa commit tại thời điểm tạo tài liệu; xem `NEXT_STEPS.md`.
# CẬP NHẬT ƯU TIÊN — 2026-07-01

Phần này là trạng thái hiện tại và được ưu tiên khi khác với mô tả cũ bên dưới.

- Kho dùng `InventoryItem`/`InventoryStock` làm nguồn tồn chuẩn cho ProductVariant và ExtraService có `trackStock=true`.
- Đã bổ sung giá vốn bình quân, giá trị tồn, hành vi kho (`SELLABLE`, `CONSUMABLE`, `RENTAL`, `SERVICE`), lợi nhuận gộp và lifecycle xuất bản catalog.
- Product/ExtraService mới không được tạo tồn trực tiếp; tồn chỉ tăng khi phiếu nhập được ghi sổ. CSV ProductVariant yêu cầu `stock=0`.
- Import CSV hiện hỗ trợ Category, Brand, Size, Product, ProductVariant, ảnh sản phẩm, sân, ảnh sân, bảng giá, ngày lễ, giá ngày lễ, ExtraService, Supplier, InventoryItem và StockImport.
- Giờ sân là `07:00–23:45`, slot 90 phút; slot cuối `22:00–23:30`. Khoảng `23:30–23:45` là thời gian đệm và không hiển thị thành slot.
- Lịch sân admin đã sửa điều kiện sinh slot để loại bỏ slot lẻ `23:30–23:45`.
- Backend `mvn test` và frontend production build đã thành công sau thay đổi.

## Snapshot database đã kiểm tra

- 5 Category, 7 Brand, 11 Size, 15 Product, 49 ProductVariant, 15 ảnh sản phẩm.
- 15 Product đều `ACTIVE`, có ảnh primary, biến thể, tồn và giá vốn; 15 URL ảnh sản phẩm là duy nhất.
- 15 ExtraService: 13 theo dõi tồn, 2 dịch vụ không theo dõi tồn.
- 62 InventoryItem và 62 InventoryStock; không có liên kết mồ côi, tồn âm, lệch tồn khả dụng, lệch giá trị tồn hoặc tồn dương có giá vốn bằng 0.
- 5 Supplier; 7 phiếu nhập đã ghi sổ, 62 dòng, tổng giá trị `392.860.000`.
- 10 sân và 10 ảnh sân; hiện các sân dùng chung một URL ảnh tham khảo.
- 280 bảng giá thường, 10 ngày lễ, 400 bảng giá ngày lễ; không có gap/overlap.
- Còn 58 item có ngưỡng cảnh báo bằng 0: 49 ProductVariant và 9 ExtraService tiêu hao.
# FLOW BOOKING/CHECKOUT ỔN ĐỊNH — 2026-07-01

- Checkout giỏ hàng tính discount tại backend từ giá snapshot trong cart và discount hiện tại; OrderItem lưu `unitPrice`, `discountAmount`, `finalPrice`, `total`.
- Order chỉ hỗ trợ `COD` và `VIETQR`; booking chỉ hỗ trợ `CASH` và `VIETQR`. VNPay tạm tắt cho đến khi có callback và chữ ký.
- VietQR dùng trạng thái `UNPAID -> PENDING -> PAID`; khách gửi xác nhận, admin xác nhận tiền riêng rồi mới confirm booking/order.
- Booking pending tự hết hạn sau 15 phút; order VietQR pending tự hủy sau 30 phút. Scheduler hoàn reservation và cập nhật payment.
- Xóa booking/order từ admin đã chuyển thành cancel an toàn, không hard-delete dữ liệu nghiệp vụ.
- Booking API chỉ nhận slot đúng 90 phút trên lưới bắt đầu từ `07:00`; slot cuối `22:00–23:30`.
- Backend có test mới cho slot policy và order price calculator.
