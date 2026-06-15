package vn.phankyanh.shopbongda.maintenance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.phankyanh.shopbongda.maintenance.dto.request.MaintenanceRequest;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceResponse;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceService {

    MaintenanceResponse createMaintenance(MaintenanceRequest request);

    MaintenanceResponse getMaintenanceById(Long id);

    Page<MaintenanceResponse> getAllMaintenances(String keyword, Pageable pageable);

    List<MaintenanceResponse> getMaintenancesByFieldId(Long fieldId);

    MaintenanceResponse updateMaintenance(Long id, MaintenanceRequest request);

    void deleteMaintenance(Long id);

    boolean isFieldAvailable(Long fieldId, LocalDateTime startTime, LocalDateTime endTime);

    MaintenanceScheduleResponse getMaintenanceStatus(Long fieldId, LocalDateTime checkTime);

    List<MaintenanceResponse> getUpcomingMaintenances(Long fieldId);
}