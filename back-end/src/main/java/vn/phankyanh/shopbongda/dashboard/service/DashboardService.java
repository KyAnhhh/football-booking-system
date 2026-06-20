package vn.phankyanh.shopbongda.dashboard.service;

import java.util.List;

import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.dashboard.dto.response.AdminOverviewResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;

public interface DashboardService  {
    DashboardSummaryResponse getDashboardSummary();

    List<RevenueChartResponse> getMonthlyRevenue();

    AdminOverviewResponse getAdminOverview(
            BookingStatus bookingStatus,
            OrderStatus orderStatus
    );
}
