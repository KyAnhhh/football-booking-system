package vn.phankyanh.shopbongda.booking.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.dto.request.BookingRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/bookings")
public class UserBookingController {
    private final BookingService bookingService;
    
    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(bookingService.createBooking(request, userId));
        return response;
    }

    @GetMapping
    public ApiResponse<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>();
        response.setData(bookingService.getBookingsByUserId(userId));
        return response;
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<BookingResponse> getMyBookingDetail(
            @PathVariable("bookingId") Long bookingId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(
                bookingService.getBookingByIdForUser(
                        bookingId,
                        userId
                )
        );
        return response;
    }

    @PatchMapping("/{bookingId}/cancel")
    public ApiResponse<Void> cancelBooking(@PathVariable("bookingId") Long bookingId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<Void> response = new ApiResponse<>();
        this.bookingService.cancelBooking(bookingId, userId);
        return response;
    }

    @PatchMapping("/{bookingId}/payment-submitted")
    public ApiResponse<BookingResponse> markPaymentSubmitted(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(
                bookingService.markPaymentSubmitted(
                        bookingId,
                        jwt.getClaim("id")
                )
        );
        return response;
    }
}
