# Next Steps

## Ưu tiên rất cao

1. **Rotate và externalize secrets.** `application.yaml` đang chứa Gmail app password và JWT secret; chuyển sang environment/secret manager, thu hồi credential hiện tại. Cloudinary config cũng cần kiểm tra nguồn secret.
2. **Audit các endpoint public có mutation/data nhạy cảm.**
   - `/api/public/images/upload` cho phép upload không xác thực.
   - `/api/public/orderItems` có GET/PUT/DELETE.
   - `/api/public/payments/{id}` có thể lộ payment.
   - profile nằm dưới `/api/public/users/...`; xác minh controller luôn yêu cầu principal.
3. **Hoàn thiện/kiểm tra VNPay return flow.** `return-url` trỏ `/api/payments/vnpay-return` nhưng không thấy mapping tương ứng trong `PaymentController`; chưa thấy verify signature/cập nhật booking payment callback đầy đủ.
4. **Test module inventory đang thay đổi.** Worktree lúc lập tài liệu có modified/untracked files cho stock-import lifecycle, consistency và migration V19. Không ghi đè chúng; cần review diff, compile, migration test và transactional test trước commit.

## Tính đúng đắn nghiệp vụ

- Viết integration/concurrency tests cho:
  - hai user đặt cùng sân/slot;
  - booking tạo/hủy/confirm/check-in/check-out;
  - voucher total/per-user limit và refund;
  - inventory reserve/consume/release khi order/booking thất bại;
  - stock import draft → posted/cancelled và idempotency;
  - order status/payment transitions.
- Rà soát `@Transactional`, locking và unique constraint cho các luồng trên.
- Chuẩn hóa state machine booking/order; đảm bảo controller không thể bỏ qua transition hợp lệ.
- Kiểm tra mọi `return null` trong `ImportServiceImpl`: nhiều chỗ có vẻ là parse sentinel, cần test phân biệt “ô trống hợp lệ” và “dữ liệu lỗi”.
- Kiểm tra worker đồng bộ ProductVariant ↔ Inventory tránh vòng lặp/update mất dữ liệu.

## Database và migration

- Project có SQL `db/migration/V2...V19` nhưng `pom.xml` không thấy Flyway và JPA đang `ddl-auto: update`. Chọn một nguồn schema chuẩn:
  - thêm Flyway, baseline schema/V1 rõ ràng, đặt `ddl-auto: validate`; hoặc
  - ghi rõ migrations được chạy thủ công.
- Xác minh thứ tự migration theo version số (không theo lexical filename).
- Test V19 trên database có dữ liệu V18, đặc biệt status/default/backfill.
- Không commit demo/production secret hoặc dữ liệu cá nhân.

## Backend quality

- Thay `RuntimeException` bằng exception nghiệp vụ có mã HTTP/error body ổn định; mở rộng `GlobalExceptionHandler`.
- Bổ sung Bean Validation và ownership checks thống nhất.
- Rà soát DTO/entity exposure và N+1/lazy-loading ở dashboard, booking, order.
- Xóa dependency JWT trùng nếu JJWT thực sự không dùng; cập nhật MapStruct nếu cần tương thích Java/Spring hiện tại.
- Tránh hard-code sender email trong `EmailService`.
- Bổ sung pagination cho các list có thể lớn (notification, booking/order tùy endpoint).
- `combo/` trống: xóa nếu không dùng hoặc triển khai và ghi rõ scope.
- `WishList` chỉ có entity: quyết định triển khai repository/service/API/UI hoặc loại bỏ.
- `FootballFieldImage` thiếu luồng CRUD rõ ràng: xác minh ảnh được quản lý qua field request hay cần API riêng.

## Frontend

- Đưa API base URL vào `.env`/config duy nhất; không hard-code trong từng service.
- Xóa log refresh token trong `authService.refresh`; sửa `console.log(refresh.status)` vì `refresh` là function.
- Bọc các client route cần đăng nhập (`profile`, cart, checkout, my orders/bookings, review/payment) bằng `ProtectedRoute` hoặc auth guard client phù hợp.
- Thêm route/menu cho `ProductVariant.js`, `ImagePage.js` nếu vẫn cần; nếu không, xóa dead page sau khi xác nhận.
- Xây UI membership tier nếu chức năng membership nằm trong scope.
- Thêm loading/error/empty-state nhất quán và error boundary.
- Cân nhắc query/cache layer để tránh fetch trùng và stale state; notification hiện không realtime.
- Sửa typo `RatingSumary.js` khi có thể (kèm cập nhật imports).

## Testing và vận hành

- Backend hiện chỉ có context-load test; thêm unit/service, `@WebMvcTest`, repository và integration tests (Testcontainers MySQL/Redis).
- Frontend kiểm tra lại `App.test.js`, thêm test route guards, token refresh queue, booking/checkout forms.
- Thêm CI chạy `mvn test`, frontend test/build và migration validation.
- Chạy dependency/security scan; xác minh package bất thường `react-script` bên cạnh `react-scripts`.
- Bổ sung profile config dev/test/prod, logging phù hợp và tắt `show-sql` ngoài dev.
- Cập nhật `back-end/API_DOCUMENTATION.md` từ controller hiện tại hoặc sinh OpenAPI để tránh lệch tài liệu.

## Câu hỏi cần xác nhận với product owner

- Membership/loyalty và wishlist có nằm trong release không?
- Order dùng VietQR thủ công, VNPay, hay cả hai? Booking thanh toán online có cần callback webhook chuẩn?
- Staff có được xem/đổi extra services và payment hay chỉ manager?
- Xóa booking/order/payment là hard delete hay cần audit/soft delete?
- Inventory là source of truth cho mọi product variant và extra service từ thời điểm nào?

