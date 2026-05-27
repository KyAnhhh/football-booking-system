package vn.phankyanh.shopbongda.dashboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;
import vn.phankyanh.shopbongda.dashboard.service.DashboardService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/public/dashboard")
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
}