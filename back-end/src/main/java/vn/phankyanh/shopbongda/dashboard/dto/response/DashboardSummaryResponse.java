package vn.phankyanh.shopbongda.dashboard.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSummaryResponse {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Long totalBooking;
    private BigDecimal totalRevenue;
    private BigDecimal bookingRevenue;
    private BigDecimal extraServiceRevenue;
    private BigDecimal orderRevenue;
    private Long totalProductsSold;
    private Long totalExtraServicesSold;
}
