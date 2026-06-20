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
    private BigDecimal todayBookingRevenue;
    private Long todayBookings;
    private Long activeMaintenances;
    private Long newOrders;
    private List<BookingItem> todayBookingItems;
    private List<MaintenanceItem> activeMaintenanceItems;
    private List<OrderItem> newOrderItems;

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
}
