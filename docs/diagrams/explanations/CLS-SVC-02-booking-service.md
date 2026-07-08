# CLS-SVC-02 Booking Service

## Mục đích

Class diagram này mô tả các service và phương thức nghiệp vụ chính trong flow đặt sân: tạo booking, kiểm tra slot, tính giá, áp dụng voucher/membership, xác nhận thanh toán, check-in/check-out và hủy booking.

## Class/service

- `BookingService`
- `PricingService`
- `VoucherService`
- `MembershipService`
- `PaymentService`
- `RefundService`
- Entity rút gọn: `Booking`, `FootballField`, `BookingExtraService`, `ExtraService`, `BookingStatusHistory`

## Phương thức nghiệp vụ chính

- `createBooking`, `createBookingForAdmin`, `getAvailableSlots`, `markPaymentSubmitted`
- `confirmBooking`, `updateBookingStatus`, `checkInBooking`, `checkOutBooking`, `cancelBooking`
- `calculatePrice`, `calculateSlotPrice`, `generateSlotStartTimes`
- `applyVoucher`, `consumeVoucher`, `recordUsage`, `refundVoucherForBooking`
- `applyMembershipDiscount`, `addPoints`, `updateMembershipTier`

## Ghi chú rút gọn

Diagram tập trung vào service nghiệp vụ, không vẽ tất cả DTO/repository. Các entity chỉ giữ field nhận diện trạng thái, thời gian, giá và thông tin dịch vụ để hình vẫn vừa khổ A4.

## Đoạn đưa vào báo cáo

Nghiệp vụ đặt sân được điều phối bởi `BookingService`, trong đó service này phối hợp với pricing, voucher, membership, payment và refund. Khi người dùng chọn sân và khung giờ, hệ thống kiểm tra slot khả dụng và tính giá dựa trên rule giá hiện tại. Voucher và membership được áp dụng để giảm giá trước khi tạo booking. Trong giai đoạn vận hành, admin hoặc staff có thể xác nhận thanh toán, check-in, check-out hoặc cập nhật trạng thái booking. Nếu booking đã thanh toán bị hủy, hệ thống chuyển sang luồng refund để xử lý hoàn tiền.
