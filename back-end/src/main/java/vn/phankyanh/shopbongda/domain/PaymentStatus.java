package vn.phankyanh.shopbongda.domain;

public enum PaymentStatus {
    UNPAID,        // Chưa thanh toán (COD, chuyển khoản chưa nhận)
    PAID,          // Đã thanh toán thành công
    PENDING,       // Đang chờ xác nhận (online, shipper chưa báo)
    FAILED,        // Thanh toán thất bại
    REFUNDED       // Đã hoàn tiền
}
