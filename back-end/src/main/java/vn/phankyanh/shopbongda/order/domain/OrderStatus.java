package vn.phankyanh.shopbongda.order.domain;

public enum OrderStatus {
    PENDING,        // Chờ xác nhận
    CONFIRMED,      // Đã xác nhận
    SHIPPING,       // Đang giao
    DELIVERED,      // Đã giao
    COMPLETED,      // Hoàn thành, xác nhận đã nhận hàng
    CANCELLED,      // Đã huỷ
}