package vn.phankyanh.shopbongda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.service.DashboardService;

@RestController
@RequiredArgsConstructor
public class DashboardController  {
    private final DashboardService dashboardService;

    @GetMapping("/api/admin/dashboard-summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        ApiResponse<DashboardSummaryResponse> response = new ApiResponse<>();
        response.setData(dashboardService.getDashboardSummary());
        response.setMessage("Lấy dữ liệu dashboard thành công");
        return ResponseEntity.ok(response);
    }

}
