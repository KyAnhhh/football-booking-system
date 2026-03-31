package vn.phankyanh.shopbongda.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.user.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.user.service.DashboardService;

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
