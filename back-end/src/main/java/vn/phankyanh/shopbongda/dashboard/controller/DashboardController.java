package vn.phankyanh.shopbongda.dashboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.dashboard.dto.response.AdminOverviewResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;
import vn.phankyanh.shopbongda.dashboard.service.DashboardService;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse>
    getDashboardSummary() {

        ApiResponse<DashboardSummaryResponse>
                response = new ApiResponse<>();

        response.setData(
                dashboardService.getDashboardSummary()
        );

        return response;
    }

    @GetMapping("/monthly-revenue")
    public ApiResponse<List<RevenueChartResponse>>
    getMonthlyRevenue() {

        ApiResponse<List<RevenueChartResponse>>
                response = new ApiResponse<>();

        response.setData(
                dashboardService.getMonthlyRevenue()
        );

        return response;
    }

    @GetMapping("/admin-overview")
    public ApiResponse<AdminOverviewResponse> getAdminOverview(
            @RequestParam(required = false) BookingStatus bookingStatus,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {

        ApiResponse<AdminOverviewResponse> response =
                new ApiResponse<>();

        response.setData(
                dashboardService.getAdminOverview(
                        bookingStatus,
                        orderStatus
                )
        );

        return response;
    }
}
