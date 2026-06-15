package vn.phankyanh.shopbongda.maintenance.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MaintenanceRequest {

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be present or future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private String reason;

    private String description;
}