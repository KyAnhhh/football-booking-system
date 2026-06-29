# Project Context — Football Booking System

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

