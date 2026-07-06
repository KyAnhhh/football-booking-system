# Next Steps

> Cập nhật gần nhất: 2026-07-01. Đọc phần **CẬP NHẬT NEXT STEPS** ở cuối file trước.

## Ưu tiên rất cao

1. **Rotate và externalize secrets.** `application.yaml` đang chứa Gmail app password và JWT secret; chuyển sang environment/secret manager, thu hồi credential hiện tại. Cloudinary config cũng cần kiểm tra nguồn secret.
2. **Audit các endpoint public có mutation/data nhạy cảm.**
   - Đã xóa `/api/public/images/upload`; upload ảnh sản phẩm dùng endpoint admin.
   - `/api/public/orderItems` có GET/PUT/DELETE.
   - `/api/public/payments/{id}` có thể lộ payment.
   - profile nằm dưới `/api/public/users/...`; xác minh controller luôn yêu cầu principal.
3. **Thanh toán online.** VNPay đã được loại khỏi phạm vi; duy trì và kiểm thử flow VietQR xác nhận thủ công.
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
- `combo/` và `common/` đã được xóa.
- Wishlist đã có repository/service/API/UI; cần bổ sung integration test.
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
# CẬP NHẬT NEXT STEPS — 2026-07-01

1. Đặt ngưỡng cảnh báo cho 58 item: 49 ProductVariant và 9 ExtraService tiêu hao. Bốn Extra `RENTAL` đã có threshold.
2. Quyết định ranh giới giá cuối ngày: slot cuối bắt đầu `22:00`, nhưng rule cuối hiện bắt đầu `22:30`. Nếu cần giá riêng cho slot cuối, đổi rule trước kết thúc `22:00` và rule cuối thành `22:00–23:45`.
3. Thay 10 ảnh sân tham khảo đang dùng chung URL bằng ảnh riêng.
4. Tạo Voucher và MembershipTier nếu cần kiểm thử; database hiện chưa có dữ liệu hai module này.
5. Viết test cho nhiều lô giá nhập, reserve/commit/release, hủy/return, Extra tiêu hao/cho thuê và phân bổ voucher vào lợi nhuận.
6. Thiết kế khấu hao/chi phí theo lượt thuê cho `RENTAL`; hiện giá vốn mỗi lượt là 0.
7. Chốt schema management: project có migration đến V22 nhưng chưa cấu hình Flyway và vẫn dùng `ddl-auto: update`.
8. Commit các thay đổi local inventory/catalog/import/calendar hiện còn modified hoặc untracked; không rollback nhầm.
# SAU KHI ỔN ĐỊNH FLOW — 2026-07-01

- Không còn source/config VNPay; chỉ bổ sung lại trong một thay đổi triển khai end-to-end riêng.
- Cần thêm integration test dùng database test cho hai request đồng thời đặt cùng slot/mua cùng tồn cuối.
- Cần test scheduler bằng clock giả thay vì chờ thời gian thật.
- `REFUNDED` hiện là trạng thái nghiệp vụ nội bộ; chưa kết nối API hoàn tiền ngân hàng.
- Sau khi push nên chạy smoke test thủ công: booking CASH, booking VietQR, order COD, order VietQR, cancel và expiration.
- Cần integration test database cho VietQR: submit → admin xác nhận tiền → xác nhận booking/order → commit kho; timeout không được hủy payment `PAID`.
- Nên bổ sung `completedAt` riêng cho Booking/Order nếu cần báo cáo kế toán chính xác tuyệt đối theo thời điểm hoàn tất; hiện order dùng `updatedAt`, booking dùng `createdAt` làm mốc nhóm tháng.
