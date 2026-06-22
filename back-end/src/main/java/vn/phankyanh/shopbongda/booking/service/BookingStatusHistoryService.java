package vn.phankyanh.shopbongda.booking.service;

import java.util.List;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.response.BookingStatusHistoryResponse;
import vn.phankyanh.shopbongda.user.domain.User;

public interface BookingStatusHistoryService {

    void record(
            Booking booking,
            BookingStatus oldStatus,
            BookingStatus newStatus,
            User changedBy,
            String note
    );

    List<BookingStatusHistoryResponse> getByBookingId(Long bookingId);
}
