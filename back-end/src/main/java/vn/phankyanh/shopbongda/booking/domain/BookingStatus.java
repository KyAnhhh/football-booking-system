package vn.phankyanh.shopbongda.booking.domain;

public enum BookingStatus {
    PENDING,       // vừa đặt
    CONFIRMED,     // đã xác nhận (có thể đã thanh toán)
    CANCELLED,     
    IN_PROGRESS,   // đang đá
    COMPLETED,     // đá xong
    EXPIRED        // quá giờ chưa thanh toán
}
