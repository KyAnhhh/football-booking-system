package vn.phankyanh.shopbongda.maintenance.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class MaintenanceTimePolicy {

    private MaintenanceTimePolicy() {
    }

    public static void validateForCreate(
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime now
    ) {
        validateRange(startTime, endTime);
        if (startTime.isBefore(now.truncatedTo(ChronoUnit.MINUTES))) {
            throw new IllegalArgumentException(
                    "Thời gian bắt đầu bảo trì không được ở trong quá khứ"
            );
        }
    }

    public static void validateRange(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Thời gian bắt đầu và kết thúc là bắt buộc"
            );
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Thời gian kết thúc phải sau thời gian bắt đầu"
            );
        }
    }
}
