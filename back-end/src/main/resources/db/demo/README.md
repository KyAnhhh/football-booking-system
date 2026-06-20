# Demo data

File `V1__demo_data.sql` là seed data thủ công cho môi trường local/dev.

## Cách chạy

Chạy trong MySQL với database `shopbongda`:

```sql
SOURCE D:/IdeaProjects/football-ecommerce/back-end/src/main/resources/db/demo/V1__demo_data.sql;
```

Hoặc copy nội dung file vào MySQL Workbench rồi execute.

Tài khoản demo:

- Admin: `demo_admin` / `password`
- User: `demo_user` / `password`

## Dữ liệu đã có

- 4 membership tiers.
- 2 users demo.
- 6 sân bóng.
- Pricing rule theo 7 ngày, 4 slot mỗi ngày, slot 90 phút.
- 2 ngày holiday. Ngày `2026-07-27` dùng để test holiday pricing.
- 4 extra services.
- 3 vouchers: `WELCOME10`, `FIELD50K`, `WEEKEND5`.
- 2 maintenance windows để test chặn slot.
- 16 sản phẩm, có discount, image, variant và stock.

## Case test nhanh

1. Product pagination: mở trang sản phẩm client, phải thấy 16 sản phẩm và có trang tiếp theo nếu page size là 10.
2. Booking thường: đặt `Sân Cỏ Sao Mai 5A` ngày `2026-07-01`, slot `18:00-19:30`.
3. Maintenance: thử đặt `Sân Cỏ Sao Mai 5A` ngày `2026-07-02`, slot `18:00-19:30`, slot này phải bị chặn.
4. Holiday pricing: đặt ngày `2026-07-27`, giá phải dùng rule Chủ nhật.
5. Extra: thêm `Thuê bóng thi đấu` hoặc `Nước suối thùng`, breakdown phải cộng thêm extra.
6. Voucher: nhập `WELCOME10` hoặc `FIELD50K`, breakdown phải trừ voucher và giảm quantity.
7. Membership: user `demo_user` đang ở tier `Vàng`, booking phải có membership discount.

## Checklist demo theo luồng

### Client shop

1. Vào trang sản phẩm.
2. Kiểm tra có 16 sản phẩm, một số sản phẩm có badge giảm giá.
3. Sang trang 2 nếu page size là 10.
4. Mở chi tiết sản phẩm.
5. Chọn variant còn tồn kho.
6. Thêm vào giỏ hàng.
7. Vào checkout và kiểm tra giá sau giảm giá.

### Booking sân

1. Đăng nhập bằng `demo_user`.
2. Vào danh sách sân, phải thấy 6 sân.
3. Mở `Sân Cỏ Sao Mai 5A`.
4. Chọn ngày `2026-07-01`, slot `18:00-19:30`.
5. Thêm extra `Thuê bóng thi đấu`.
6. Nhập voucher `WELCOME10`.
7. Kiểm tra breakdown:
   - Giá sân lấy từ pricing rule.
   - Extra được cộng vào.
   - Membership tier `Vàng` được trừ.
   - Voucher được trừ sau membership.
   - Tổng thanh toán là final price.
8. Tạo booking bằng phương thức `CASH` hoặc `COD`.

### Maintenance

1. Mở `Sân Cỏ Sao Mai 5A`.
2. Chọn ngày `2026-07-02`.
3. Slot `18:00-19:30` phải bị chặn do maintenance.

### Holiday pricing

1. Mở một sân bất kỳ.
2. Chọn ngày `2026-07-27`.
3. Slot hiển thị giá theo rule Chủ nhật.

### Admin

1. Đăng nhập bằng `demo_admin`.
2. Kiểm tra các trang:
   - Quản lý sân bóng.
   - Quản lý bảng giá.
   - Quản lý bảo trì.
   - Quản lý voucher.
   - Quản lý dịch vụ thêm.
   - Quản lý ngày lễ.
   - Quản lý sản phẩm.
3. Kiểm tra table, nút, modal, pagination và empty state giữ cùng phong cách.
