package vn.phankyanh.shopbongda.maintenance.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.maintenance.domain.Maintenance;
import vn.phankyanh.shopbongda.maintenance.dto.request.MaintenanceRequest;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceResponse;
import vn.phankyanh.shopbongda.maintenance.dto.response.MaintenanceScheduleResponse;
import vn.phankyanh.shopbongda.maintenance.repository.MaintenanceRepository;
import vn.phankyanh.shopbongda.maintenance.service.MaintenanceService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final FootballFieldRepository footballFieldRepository;

    @Override
    @Transactional
    public MaintenanceResponse createMaintenance(MaintenanceRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        FootballField field = footballFieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Field not found"));

        List<Maintenance> conflicts = maintenanceRepository.findConflictingMaintenances(
                request.getFieldId(), request.getStartTime(), request.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Maintenance schedule conflicts with existing maintenance");
        }

        Maintenance maintenance = Maintenance.builder()
                .fieldId(request.getFieldId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reason(request.getReason())
                .description(request.getDescription())
                .build();

        Maintenance saved = maintenanceRepository.save(maintenance);
        return mapToResponse(saved, field.getName());
    }

    @Override
    public MaintenanceResponse getMaintenanceById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));

        String fieldName = footballFieldRepository.findById(maintenance.getFieldId())
                .map(FootballField::getName)
                .orElse("Unknown");

        return mapToResponse(maintenance, fieldName);
    }

    @Override
    public Page<MaintenanceResponse> getAllMaintenances(String keyword, Pageable pageable) {
        return maintenanceRepository.findAll(pageable).map(m -> {
            String fieldName = footballFieldRepository.findById(m.getFieldId())
                    .map(FootballField::getName)
                    .orElse("Unknown");
            return mapToResponse(m, fieldName);
        });
    }

    @Override
    public List<MaintenanceResponse> getMaintenancesByFieldId(Long fieldId) {
        return maintenanceRepository.findByFieldId(fieldId).stream()
                .map(m -> mapToResponse(m, footballFieldRepository.findById(fieldId)
                        .map(FootballField::getName).orElse("Unknown")))
                .toList();
    }

    @Override
    @Transactional
    public MaintenanceResponse updateMaintenance(Long id, MaintenanceRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));

        validateTimeRange(request.getStartTime(), request.getEndTime());

        List<Maintenance> conflicts = maintenanceRepository.findConflictingMaintenances(
                request.getFieldId(), request.getStartTime(), request.getEndTime());
        conflicts.removeIf(m -> m.getId().equals(id));
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Maintenance schedule conflicts with existing maintenance");
        }

        maintenance.setFieldId(request.getFieldId());
        maintenance.setStartTime(request.getStartTime());
        maintenance.setEndTime(request.getEndTime());
        maintenance.setReason(request.getReason());
        maintenance.setDescription(request.getDescription());

        Maintenance updated = maintenanceRepository.save(maintenance);

        String fieldName = footballFieldRepository.findById(request.getFieldId())
                .map(FootballField::getName).orElse("Unknown");

        return mapToResponse(updated, fieldName);
    }

    @Override
    @Transactional
    public void deleteMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        maintenanceRepository.delete(maintenance);
    }

    @Override
    public boolean isFieldAvailable(Long fieldId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Maintenance> conflicts = maintenanceRepository.findConflictingMaintenances(
                fieldId, startTime, endTime);
        return conflicts.isEmpty();
    }

    @Override
    public MaintenanceScheduleResponse getMaintenanceStatus(Long fieldId, LocalDateTime checkTime) {
        List<Maintenance> active = maintenanceRepository.findActiveMaintenanceAt(fieldId, checkTime);

        if (active.isEmpty()) {
            return MaintenanceScheduleResponse.builder()
                    .isUnderMaintenance(false)
                    .build();
        }

        Maintenance m = active.get(0);
        return MaintenanceScheduleResponse.builder()
                .isUnderMaintenance(true)
                .maintenanceStartTime(m.getStartTime())
                .maintenanceEndTime(m.getEndTime())
                .maintenanceReason(m.getReason())
                .build();
    }

    @Override
    public List<MaintenanceResponse> getUpcomingMaintenances(Long fieldId) {
        return maintenanceRepository.findByFieldIdAndStartTimeAfter(fieldId, LocalDateTime.now())
                .stream()
                .map(m -> mapToResponse(m, footballFieldRepository.findById(fieldId)
                        .map(FootballField::getName).orElse("Unknown")))
                .toList();
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private MaintenanceResponse mapToResponse(Maintenance maintenance, String fieldName) {
        return MaintenanceResponse.builder()
                .id(maintenance.getId())
                .fieldId(maintenance.getFieldId())
                .fieldName(fieldName)
                .startTime(maintenance.getStartTime())
                .endTime(maintenance.getEndTime())
                .reason(maintenance.getReason())
                .description(maintenance.getDescription())
                .createdAt(maintenance.getCreatedAt())
                .updatedAt(maintenance.getUpdatedAt())
                .build();
    }
}
