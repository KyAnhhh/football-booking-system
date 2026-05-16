package vn.phankyanh.shopbongda.booking.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.request.BookingRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;
    
    @PostMapping("/user/bookings")
    public ApiResponse<BookingResponse> createBooking(@RequestBody BookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(bookingService.createBooking(request, userId));
        return response;
    }

    @GetMapping("/admin/bookings")
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

    @GetMapping("/user/bookings")
    public ApiResponse<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>();
        response.setData(bookingService.getBookingsByUserId(userId));
        return response;
    }

    @PatchMapping("/admin/bookings/{bookingId}/confirm")
    public ApiResponse<Void> confirmBooking(@PathVariable("bookingId") Long bookingId) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.confirmBooking(bookingId);
        return response;
    }
    
    @PatchMapping("/user/bookings/{bookingId}/cancel")
    public ApiResponse<Void> cancelBooking(@PathVariable("bookingId") Long bookingId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.cancelBooking(bookingId, userId);
        return response;
    }
    
    @DeleteMapping("/admin/bookings/{bookingId}")
    public ApiResponse<Void> deleteBooking(@PathVariable("bookingId") Long bookingId) {
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.deleteBooking(bookingId);
        return response;
    }
}
