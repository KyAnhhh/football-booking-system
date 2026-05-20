package vn.phankyanh.shopbongda.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {
    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<Page<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BookingStatus status
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BookingResponse> result = bookingService.getAllBooking(
            keyword,
            status,
            pageable
        );

        ApiResponse<Page<BookingResponse>> response = new ApiResponse<>();
        response.setData(result);
        return response;
    }

    @PatchMapping("/{bookingId}/confirm")
    public ApiResponse<Void> confirmBooking(@PathVariable("bookingId") Long bookingId) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.confirmBooking(bookingId);
        return response;
    }

    @DeleteMapping("/{bookingId}")
    public ApiResponse<Void> deleteBooking(@PathVariable("bookingId") Long bookingId) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.deleteBooking(bookingId);
        return response;
    }
}
