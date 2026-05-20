package vn.phankyanh.shopbongda.booking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.request.BookingRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.field.dto.response.SlotResponse;

public interface BookingService {

    BookingResponse createBooking(
            BookingRequest request,
            Long userId
    );

    Page<BookingResponse> getAllBooking(
            String keyword,
            BookingStatus status,
            Pageable pageable
    );

    List<BookingResponse> getBookingsByUserId(
            Long userId
    );

    List<SlotResponse> getAvailableSlots(
            Long fieldId,
            LocalDate date
    );

    BookingResponse getBookingForQr(
            Long bookingId
    );

    void confirmBooking(
            Long bookingId
    );

    void cancelBooking(
            Long bookingId,
            Long userId
    );

    void deleteBooking(
            Long bookingId
    );
}