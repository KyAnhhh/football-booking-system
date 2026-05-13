package vn.phankyanh.shopbongda.booking.projection;

import java.time.LocalDateTime;

import vn.phankyanh.shopbongda.booking.domain.BookingStatus;

public interface BookingTimeView {
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    BookingStatus getStatus();
}
