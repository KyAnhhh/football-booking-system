package vn.phankyanh.shopbongda.maintenance.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MaintenanceTimePolicyTest {

    @Test
    void acceptsStartInCurrentMinuteDespiteServerSeconds() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 2, 14, 30, 45);
        LocalDateTime start = LocalDateTime.of(2026, 7, 2, 14, 30);

        assertDoesNotThrow(() -> MaintenanceTimePolicy.validateForCreate(
                start,
                start.plusHours(2),
                now
        ));
    }

    @Test
    void rejectsPastStartAndInvalidRange() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 2, 14, 30, 45);

        assertThrows(
                IllegalArgumentException.class,
                () -> MaintenanceTimePolicy.validateForCreate(
                        now.minusMinutes(1),
                        now.plusHours(1),
                        now
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> MaintenanceTimePolicy.validateRange(now, now)
        );
    }
}
