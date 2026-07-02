package vn.phankyanh.shopbongda.booking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class BookingSlotPolicy {

    public static final int SLOT_MINUTES = 90;
    public static final LocalTime OPEN_TIME = LocalTime.of(7, 0);
    public static final LocalTime CLOSE_TIME = LocalTime.of(23, 45);

    private BookingSlotPolicy() {
    }

    public static void validate(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null
                || !start.toLocalDate().equals(end.toLocalDate())
                || Duration.between(start, end).toMinutes() != SLOT_MINUTES) {
            throw new IllegalArgumentException(
                    "Booking phải đúng một slot 90 phút"
            );
        }

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        if (startTime.isBefore(OPEN_TIME)
                || endTime.isAfter(CLOSE_TIME)) {
            throw new IllegalArgumentException(
                    "Booking phải nằm trong 07:00-23:45"
            );
        }

        long minutesFromOpen =
                Duration.between(OPEN_TIME, startTime).toMinutes();
        if (minutesFromOpen % SLOT_MINUTES != 0) {
            throw new IllegalArgumentException(
                    "Thời gian bắt đầu không khớp slot 90 phút"
            );
        }
    }
}
