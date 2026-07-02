# Football Booking System

Hệ thống full-stack quản lý đặt sân bóng, bán sản phẩm thể thao, dịch vụ đi kèm và kho hàng. Repository gồm Spring Boot REST API và React SPA cho khách hàng, nhân viên và quản trị viên.

## Nội dung

- [Chức năng chính](#chức-năng-chính)
- [Kiến trúc và công nghệ](#kiến-trúc-và-công-nghệ)
- [Cấu trúc repository](#cấu-trúc-repository)
- [Yêu cầu môi trường](#yêu-cầu-môi-trường)
- [Cài đặt và chạy local](#cài-đặt-và-chạy-local)
- [Khởi tạo dữ liệu](#khởi-tạo-dữ-liệu)
- [Các flow nghiệp vụ quan trọng](#các-flow-nghiệp-vụ-quan-trọng)
- [API và phân quyền](#api-và-phân-quyền)
- [Kiểm thử và build](#kiểm-thử-và-build)
- [Tài liệu dự án](#tài-liệu-dự-án)
- [Giới hạn hiện tại](#giới-hạn-hiện-tại)

## Chức năng chính

### Khách hàng

- Đăng ký, đăng nhập, refresh token, đăng xuất và đặt lại mật khẩu.
- Xem, tìm kiếm và lọc sản phẩm theo danh mục, thương hiệu, size.
- Chọn biến thể, thêm giỏ hàng, mua ngay hoặc checkout nhiều sản phẩm.
- Quản lý địa chỉ giao hàng và theo dõi đơn hàng.
- Thanh toán đơn hàng bằng COD hoặc VietQR.
- Xem sân, slot trống, giá theo ngày/khung giờ và lịch bảo trì.
- Đặt sân kèm dịch vụ thêm, membership và voucher.
- Thanh toán booking bằng CASH hoặc VietQR.
- Hủy booking/đơn hàng khi còn ở trạng thái cho phép.
- Đánh giá sản phẩm và sân sau khi hoàn tất.
- Nhận thông báo thay đổi trạng thái.

### Nhân viên và quản trị

- Dashboard vận hành, KPI và biểu đồ doanh thu theo tháng.
- Quản lý người dùng và vai trò.
- Quản lý category, brand, size, product, variant và ảnh.
- Kiểm tra điều kiện trước khi đưa sản phẩm/dịch vụ lên kệ.
- Quản lý sân, ảnh sân, bảng giá, ngày lễ và giá ngày lễ.
- Quản lý booking, check-in, check-out và lịch sân.
- Quản lý lịch bảo trì.
- Quản lý đơn hàng và xác nhận thanh toán VietQR.
- Quản lý voucher, membership tier, review và notification.
- Import dữ liệu hàng loạt bằng CSV.

### Kho hàng

- Inventory item, tồn thực tế, tồn giữ chỗ và tồn khả dụng.
- Supplier và phiếu nhập nhiều dòng.
- Lifecycle phiếu nhập: `DRAFT → POSTED` hoặc `CANCELLED`.
- Giá vốn bình quân và giá trị tồn kho.
- Ngưỡng cảnh báo tồn thấp theo từng item.
- Điều chỉnh tồn có lịch sử giao dịch.
- Reservation cho booking và order.
- Đối soát tính nhất quán giữa inventory, product variant và extra service.
- Hành vi kho: `SELLABLE`, `CONSUMABLE`, `RENTAL`, `SERVICE`.
- Báo cáo doanh thu, giá vốn và lợi nhuận gộp.

## Kiến trúc và công nghệ

### Backend

- Java 21.
- Spring Boot 3.5.6.
- Spring Web, Validation, Data JPA/Hibernate.
- Spring Security và OAuth2 Resource Server.
- JWT access/refresh token; Redis lưu token đã thu hồi.
- MySQL.
- Maven.
- Lombok và MapStruct.
- Apache Commons CSV.
- Cloudinary.
- ZXing QR Code.
- Spring Mail.

Backend được tổ chức theo feature:

```text
booking, calendar, dashboard, extra, field, holiday,
importer, inventory, maintenance, membership, notification,
order, payment, pricing, product, review, user, voucher
```

### Frontend

- React 19.
- Create React App và React Scripts 5.
- React Router DOM 7.
- Bootstrap và React Bootstrap.
- Recharts.
- Framer Motion.
- Lucide React.
- SweetAlert/SweetAlert2.
- Native Fetch với wrapper tự refresh JWT.

### Luồng tổng quát

```text
React SPA
   │
   │ HTTP + Bearer JWT
   ▼
Spring Boot REST API
   ├── MySQL: dữ liệu nghiệp vụ
   ├── Redis: token đã thu hồi
   ├── Cloudinary: hình ảnh
   ├── SMTP: email
   └── ZXing: QR VietQR
```

## Cấu trúc repository

```text
.
├── back-end/
│   ├── pom.xml
│   ├── docker-compose.yaml
│   └── src/
│       ├── main/java/vn/phankyanh/shopbongda/
│       ├── main/resources/
│       │   ├── application.yaml
│       │   ├── db/migration/
│       │   └── db/demo/
│       └── test/java/
├── front-end/
│   ├── package.json
│   ├── yarn.lock
│   ├── public/
│   └── src/
│       ├── components/
│       ├── contexts/
│       ├── layouts/
│       ├── pages/
│       ├── routes/
│       ├── services/
│       └── assets/styles/
├── docs/
│   ├── diagrams/
│   └── images/
├── PROJECT_CONTEXT.md
├── PROJECT_CONTEXT_BACKEND.md
├── PROJECT_CONTEXT_FRONTEND.md
├── PROJECT_CONTEXT_API.md
└── NEXT_STEPS.md
```

## Yêu cầu môi trường

- JDK 21.
- Maven 3.9+.
- Node.js 20+.
- Yarn 1.x hoặc npm tương thích.
- MySQL 8+.
- Redis 7+ hoặc Docker Desktop.

Các port mặc định:

| Thành phần | Port |
|---|---:|
| Frontend | `3000` |
| Backend | `8080` |
| MySQL | `3306` |
| Redis | `6379` |

## Cài đặt và chạy local

### 1. Clone repository

```bash
git clone <repository-url>
cd football-booking-system
```

### 2. Tạo database MySQL

```sql
CREATE DATABASE football_booking_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Project hiện dùng:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

Hibernate sẽ tạo/cập nhật bảng khi backend khởi động. Thư mục `db/migration` có các migration V2–V22 nhưng project chưa khai báo Flyway; không chạy đồng thời migration thủ công và `ddl-auto` nếu chưa xác định rõ trạng thái schema.

### 3. Chạy Redis

Từ thư mục `back-end`:

```bash
docker compose up -d
```

Hoặc sử dụng Redis local tại `localhost:6379`.

### 4. Cấu hình backend

Không commit secret thật vào repository. Có thể cấu hình bằng biến môi trường của Spring Boot:

| Biến | Ý nghĩa |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL của MySQL |
| `SPRING_DATASOURCE_USERNAME` | Tài khoản MySQL |
| `SPRING_DATASOURCE_PASSWORD` | Mật khẩu MySQL |
| `SPRING_DATA_REDIS_HOST` | Redis host |
| `SPRING_DATA_REDIS_PORT` | Redis port |
| `SPRING_MAIL_USERNAME` | Tài khoản SMTP |
| `SPRING_MAIL_PASSWORD` | App password SMTP |
| `JWT_SECRET_KEY` | Khóa ký JWT đủ mạnh |
| `VNPAY_TMN_CODE` | Mã VNPay, hiện chưa sử dụng |
| `VNPAY_SECRET_KEY` | Secret VNPay, hiện chưa sử dụng |

Ví dụ PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/football_booking_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=utf8"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
$env:SPRING_DATA_REDIS_HOST="localhost"
$env:SPRING_DATA_REDIS_PORT="6379"
$env:JWT_SECRET_KEY="replace-with-a-long-random-secret"
```

> Bảo mật: repository từng chứa credential local trong `application.yaml` và cấu hình Cloudinary. Trước khi public/deploy, cần thu hồi hoặc xoay vòng toàn bộ credential, chuyển chúng sang biến môi trường và xóa khỏi lịch sử Git nếu cần.

### 5. Chạy backend

```bash
cd back-end
mvn spring-boot:run
```

Backend chạy tại:

```text
http://localhost:8080
```

### 6. Cài và chạy frontend

```bash
cd front-end
yarn install
yarn start
```

Hoặc:

```bash
npm install
npm start
```

Frontend chạy tại:

```text
http://localhost:3000
```

Các service frontend hiện dùng API origin `http://localhost:8080`.

## Khởi tạo dữ liệu

### Tạo tài khoản quản trị đầu tiên

1. Đăng ký một tài khoản từ giao diện.
2. Trong môi trường local, cập nhật vai trò bằng MySQL:

```sql
UPDATE users
SET role = 'ROLE_ADMIN'
WHERE username = 'your-admin-username';
```

3. Đăng xuất và đăng nhập lại để JWT chứa vai trò mới.

Các vai trò:

- `ROLE_CLIENT`
- `ROLE_STAFF`
- `ROLE_MANAGER`
- `ROLE_ADMIN`

### Thứ tự import khuyến nghị

Sử dụng trang Import trong admin. Nên tạo dữ liệu theo thứ tự:

1. Category.
2. Brand.
3. Size.
4. Supplier.
5. Product.
6. Product Variant với `stock=0`.
7. Product Image.
8. Extra Service.
9. Inventory Item.
10. Stock Import.
11. Ghi sổ phiếu nhập.
12. Football Field.
13. Football Field Image.
14. Field Pricing Rule.
15. Holiday.
16. Holiday Pricing Rule.

Các loại CSV được hỗ trợ:

```text
categories
brands
sizes
products
product-variants
football-fields
product-images
football-field-images
field-pricing-rules
holidays
holiday-pricing-rules
extra-services
suppliers
inventory-items
stock-imports
```

Template CSV:

```http
GET /api/admin/imports/templates/{type}
```

Import:

```http
POST /api/admin/imports/{type}
Content-Type: multipart/form-data
```

### Nguyên tắc dữ liệu kho

- Không nhập tồn trực tiếp khi tạo product variant hoặc extra service.
- Product variant mới phải có `stock=0`.
- Tồn kho chỉ tăng khi phiếu nhập được ghi sổ.
- Một phiếu nhập thuộc một supplier và một `importCode`.
- Một phiếu có thể chứa nhiều sản phẩm/dịch vụ.
- `DRAFT` chưa tác động tồn.
- `POSTED` tăng tồn và cập nhật giá vốn bình quân.
- `CANCELLED` không được xem là phiếu đã nhập.
- Product/Extra chỉ nên publish sau khi đủ dữ liệu và đủ điều kiện kho.

Không khuyến nghị dùng `back-end/src/main/resources/db/demo/V1__demo_data.sql` cho database mới vì file này thuộc flow dữ liệu cũ và có thể không đồng bộ inventory/catalog lifecycle hiện tại.

## Các flow nghiệp vụ quan trọng

### Booking sân

- Giờ hoạt động: `07:00–23:45`.
- Slot chuẩn: 90 phút.
- Slot cuối: `22:00–23:30`.
- `23:30–23:45` là khoảng đệm, không phải slot.

```text
PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
    ├──────────────→ CANCELLED
    └──────────────→ EXPIRED
```

- Booking pending tự hết hạn sau 15 phút nếu chưa thanh toán.
- Booking đã `PAID` không bị scheduler hủy.
- Booking `IN_PROGRESS` chỉ có thể check-out sang `COMPLETED`.
- Dịch vụ `RENTAL` được hoàn lại khi booking hoàn tất.
- Hủy booking hoàn voucher và reservation/tồn liên quan.

### Đơn hàng

```text
PENDING → CONFIRMED → SHIPPING → DELIVERED → COMPLETED
    └──────────────→ CANCELLED
```

- Kho được giữ chỗ khi checkout.
- Kho được commit khi admin xác nhận đơn.
- Đơn VietQR pending tự hết hạn sau 30 phút nếu chưa thanh toán.
- Đơn đã `PAID` không bị scheduler hủy.
- Order item là snapshot bất biến; muốn đổi sản phẩm cần hủy đơn và đặt lại.
- Hủy đơn giải phóng reservation hoặc hoàn tồn đã commit.

### Thanh toán

Phương thức hiện hỗ trợ:

| Nghiệp vụ | Phương thức |
|---|---|
| Booking | `CASH`, `VIETQR` |
| Order | `COD`, `VIETQR` |

VietQR:

```text
UNPAID → PENDING → PAID
```

1. Khách quét QR.
2. Khách bấm đã chuyển khoản.
3. Payment chuyển sang `PENDING`.
4. Admin xác nhận đã nhận tiền.
5. Payment chuyển sang `PAID`.
6. Admin mới xác nhận booking/order.

VNPay đang chủ động tắt và endpoint liên quan trả `501 Not Implemented` cho đến khi có callback, xác minh chữ ký và xử lý idempotency đầy đủ.

### Doanh thu

Dashboard monthly revenue:

- Chỉ ghi nhận booking/order `COMPLETED`.
- Tách `bookingRevenue` và `orderRevenue`.
- Tổng `revenue` bằng tổng hai nguồn.
- Nhóm theo `MM/YYYY`.
- Biểu đồ hỗ trợ 6 tháng, 12 tháng và toàn bộ.
- Tháng không có doanh thu được điền `0`.

## API và phân quyền

API được chia theo namespace:

| Prefix | Truy cập |
|---|---|
| `/api/auth/**` | Công khai |
| `/api/public/**` | Chủ yếu công khai |
| `/api/user/**` | Người dùng đã đăng nhập |
| `/api/admin/**` | Staff/Manager/Admin tùy module |

Một số endpoint tiêu biểu:

| Method | Endpoint | Mục đích |
|---|---|---|
| `POST` | `/api/auth/login` | Đăng nhập |
| `POST` | `/api/auth/register` | Đăng ký |
| `GET` | `/api/public/products` | Danh sách sản phẩm |
| `GET` | `/api/public/fields/{id}/slots` | Slot sân |
| `POST` | `/api/user/bookings` | Tạo booking |
| `PATCH` | `/api/user/bookings/{id}/payment-submitted` | Báo chuyển khoản booking |
| `POST` | `/api/user/orders/checkout-cart` | Checkout giỏ hàng |
| `PUT` | `/api/user/orders/{id}/payment-submitted` | Báo chuyển khoản order |
| `PUT` | `/api/admin/payments/bookings/{id}/confirm` | Xác nhận tiền booking |
| `PUT` | `/api/admin/payments/orders/{id}/confirm` | Xác nhận tiền order |
| `GET` | `/api/admin/inventory/stocks` | Tồn kho |
| `POST` | `/api/admin/inventory/imports` | Tạo phiếu nhập |
| `POST` | `/api/admin/inventory/imports/{id}/post` | Ghi sổ phiếu nhập |
| `GET` | `/api/admin/inventory/consistency` | Kiểm tra đồng bộ |
| `GET` | `/api/public/dashboard/monthly-revenue` | Doanh thu tháng |

Danh mục đầy đủ hơn nằm tại [PROJECT_CONTEXT_API.md](PROJECT_CONTEXT_API.md).

## Kiểm thử và build

### Backend test

```bash
cd back-end
mvn test
```

Test hiện có:

- Spring context load.
- Booking slot policy.
- Order price calculator.
- Payment expiration policy.
- Monthly revenue aggregator.
- Maintenance time policy.

### Frontend production build

```bash
cd front-end
yarn build
```

Hoặc:

```bash
npm run build
```

### Smoke test trước khi push

- Booking CASH.
- Booking VietQR: submit → confirm payment → confirm booking.
- Check-in/check-out booking.
- Booking timeout và cancel.
- Order COD.
- Order VietQR: submit → confirm payment → confirm order.
- Shipping → delivered → customer confirms receipt.
- Order timeout và cancel.
- Tồn trước/sau reserve, commit, release và return.
- Giá vốn bình quân sau nhiều phiếu nhập.
- Doanh thu dashboard sau booking/order hoàn tất.

## Tài liệu dự án

- [Project Context](PROJECT_CONTEXT.md)
- [Backend Context](PROJECT_CONTEXT_BACKEND.md)
- [Frontend Context](PROJECT_CONTEXT_FRONTEND.md)
- [API Context](PROJECT_CONTEXT_API.md)
- [Next Steps](NEXT_STEPS.md)
- [PlantUML diagrams](docs/diagrams)
- [Rendered diagrams](docs/images)

Các file context phản ánh source hiện tại tốt hơn các tài liệu cũ trong repository.

## Giới hạn hiện tại

- Chưa bật Flyway dù có thư mục migration.
- `application.yaml` và Cloudinary config cần được chuyển hoàn toàn sang biến môi trường.
- API origin frontend vẫn đang hardcode `localhost:8080`.
- VNPay chưa hoàn thiện và đang tắt.
- Chưa tích hợp hoàn tiền thật; `REFUNDED` hiện là trạng thái nội bộ.
- Chưa có integration test database cho double booking và tranh chấp tồn cuối.
- `RENTAL` chưa có mô hình khấu hao/chi phí mỗi lượt.
- Một số module/page cũ chưa nối route hoặc không còn phù hợp flow mới.

Xem [NEXT_STEPS.md](NEXT_STEPS.md) để biết danh sách kiểm tra và công việc tiếp theo.

## Tác giả

- Phan Ky Anh
- GitHub: [KyAnhhh](https://github.com/KyAnhhh)
