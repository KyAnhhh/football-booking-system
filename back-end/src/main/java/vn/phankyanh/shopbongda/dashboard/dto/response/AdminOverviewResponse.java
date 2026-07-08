package vn.phankyanh.shopbongda.dashboard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewResponse {
    private BigDecimal totalRevenue;
    private BigDecimal bookingRevenue;
    private BigDecimal extraServiceRevenue;
    private BigDecimal orderRevenue;
    private BigDecimal todayBookingRevenue;
    private Long todayBookings;
    private Long activeMaintenances;
    private Long newOrders;
    private Long pendingVietQrPayments;
    private Long lowStockItems;
    private List<BookingItem> todayBookingItems;
    private List<MaintenanceItem> activeMaintenanceItems;
    private List<OrderItem> newOrderItems;
    private List<PaymentItem> pendingPaymentItems;
    private List<LowStockItem> lowStockItemList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingItem {
        private Long id;
        private String userName;
        private String fieldName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal totalPrice;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceItem {
        private Long id;
        private Long fieldId;
        private String fieldName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private Long id;
        private String customerName;
        private String phone;
        private BigDecimal totalAmount;
        private String orderStatus;
        private String paymentMethod;
        private LocalDateTime orderDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentItem {
        private Long id;
        private String targetType;
        private Long targetId;
        private String customerName;
        private BigDecimal totalAmount;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockItem {
        private Long id;
        private String itemCode;
        private String name;
        private String itemType;
        private Integer availableQuantity;
        private Integer lowStockThreshold;
        private String unit;
    }
}
