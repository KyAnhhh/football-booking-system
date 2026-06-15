package vn.phankyanh.shopbongda.maintenance.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MaintenanceScheduleResponse {

    private boolean isUnderMaintenance;
    private LocalDateTime maintenanceStartTime;
    private LocalDateTime maintenanceEndTime;
    private String maintenanceReason;
}