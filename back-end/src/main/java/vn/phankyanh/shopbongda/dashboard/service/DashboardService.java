package vn.phankyanh.shopbongda.dashboard.service;

import java.util.List;

import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;

public interface DashboardService  {
    DashboardSummaryResponse getDashboardSummary();

    List<RevenueChartResponse> getMonthlyRevenue();
}