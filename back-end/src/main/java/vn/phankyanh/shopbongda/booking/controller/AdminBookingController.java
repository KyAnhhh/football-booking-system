package vn.phankyanh.shopbongda.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.dto.request.AdminBookingRequest;
import vn.phankyanh.shopbongda.booking.dto.request.UpdateBookingStatusRequest;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.booking.dto.response.BookingStatusHistoryResponse;
import vn.phankyanh.shopbongda.booking.service.BookingService;
import vn.phankyanh.shopbongda.booking.service.BookingStatusHistoryService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {
    private final BookingService bookingService;
    private final BookingStatusHistoryService bookingStatusHistoryService;

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

    @PostMapping
    public ApiResponse<BookingResponse> createBookingForAdmin(
            @RequestBody AdminBookingRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long adminId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(
                bookingService.createBookingForAdmin(
                        request,
                        adminId
                )
        );
        return response;
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<BookingResponse> getBookingDetail(
            @PathVariable("bookingId") Long bookingId
    ) {
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(
                bookingService.getBookingDetailForAdmin(bookingId)
        );
        return response;
    }

    @GetMapping("/{bookingId}/histories")
    public ApiResponse<java.util.List<BookingStatusHistoryResponse>> getBookingHistories(
            @PathVariable("bookingId") Long bookingId
    ) {
        ApiResponse<java.util.List<BookingStatusHistoryResponse>> response = new ApiResponse<>();
        response.setData(
                bookingStatusHistoryService.getByBookingId(bookingId)
        );
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

    @PatchMapping("/{bookingId}/status")
    public ApiResponse<BookingResponse> updateBookingStatus(
            @PathVariable("bookingId") Long bookingId,
            @RequestBody UpdateBookingStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(this.bookingService.updateBookingStatus(bookingId, request, actorId));
        return response;
    }

    @PatchMapping("/{bookingId}/check-in")
    public ApiResponse<BookingResponse> checkInBooking(
            @PathVariable("bookingId") Long bookingId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(bookingService.checkInBooking(bookingId, actorId));
        return response;
    }

    @PatchMapping("/{bookingId}/check-out")
    public ApiResponse<BookingResponse> checkOutBooking(
            @PathVariable("bookingId") Long bookingId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long actorId = jwt.getClaim("id");
        ApiResponse<BookingResponse> response = new ApiResponse<>();
        response.setData(bookingService.checkOutBooking(bookingId, actorId));
        return response;
    }
}
