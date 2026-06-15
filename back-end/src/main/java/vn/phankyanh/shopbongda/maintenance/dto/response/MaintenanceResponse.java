package vn.phankyanh.shopbongda.maintenance.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MaintenanceResponse {

    private Long id;
    private Long fieldId;
    private String fieldName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}