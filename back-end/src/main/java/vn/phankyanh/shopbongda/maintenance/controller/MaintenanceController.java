package vn.phankyanh.shopbongda.maintenance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.maintenance.dto.request.MaintenanceRequest;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceResponse;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceScheduleResponse;
import vn.phankyanh.shopbongda.maintenance.service.MaintenanceService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping("/admin/maintenance")
    public ApiResponse<MaintenanceResponse> createMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        ApiResponse<MaintenanceResponse> response = new ApiResponse<>();
        response.setData(maintenanceService.createMaintenance(request));
        return response;
    }

    @GetMapping("/admin/maintenance")
    public ApiResponse<Page<MaintenanceResponse>> getAllMaintenances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MaintenanceResponse> result = maintenanceService.getAllMaintenances(keyword, pageable);

        ApiResponse<Page<MaintenanceResponse>> response = new ApiResponse<>();
        response.setData(result);
        return response;
    }

    @GetMapping("/admin/maintenance/{id}")
    public ApiResponse<MaintenanceResponse> getMaintenanceById(@PathVariable Long id) {
        ApiResponse<MaintenanceResponse> response = new ApiResponse<>();
        response.setData(maintenanceService.getMaintenanceById(id));
        return response;
    }

    @GetMapping("/admin/maintenance/field/{fieldId}")
    public ApiResponse<List<MaintenanceResponse>> getMaintenancesByFieldId(@PathVariable Long fieldId) {
        ApiResponse<List<MaintenanceResponse>> response = new ApiResponse<>();
        response.setData(maintenanceService.getMaintenancesByFieldId(fieldId));
        return response;
    }

    @PutMapping("/admin/maintenance/{id}")
    public ApiResponse<MaintenanceResponse> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequest request
    ) {
        ApiResponse<MaintenanceResponse> response = new ApiResponse<>();
        response.setData(maintenanceService.updateMaintenance(id, request));
        return response;
    }

    @DeleteMapping("/admin/maintenance/{id}")
    public ApiResponse<Void> deleteMaintenance(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        maintenanceService.deleteMaintenance(id);
        response.setData(null);
        return response;
    }

    @GetMapping("/public/fields/{fieldId}/maintenance-status")
    public ApiResponse<MaintenanceScheduleResponse> getMaintenanceStatus(
            @PathVariable Long fieldId,
            @RequestParam(required = false) String checkTime
    ) {
        ApiResponse<MaintenanceScheduleResponse> response = new ApiResponse<>();

        java.time.LocalDateTime time = (checkTime != null)
                ? java.time.LocalDateTime.parse(checkTime)
                : java.time.LocalDateTime.now();

        response.setData(maintenanceService.getMaintenanceStatus(fieldId, time));
        return response;
    }
}