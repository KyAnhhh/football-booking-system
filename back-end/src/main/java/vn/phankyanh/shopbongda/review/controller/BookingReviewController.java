package vn.phankyanh.shopbongda.review.controller;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.review.dto.request.BookingReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewResponse;
import vn.phankyanh.shopbongda.review.service.BookingReviewService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookingReviewController {
    private final BookingReviewService bookingReviewService;

    @PostMapping("user/booking-reviews/{bookingId}")
    public ApiResponse<BookingReviewResponse> createReview(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        ApiResponse<BookingReviewResponse> response = new ApiResponse<>();
        Long userId = jwt.getClaim("id");
        BookingReviewResponse reviewResponse = bookingReviewService.createReview(bookingId, request, userId);
        response.setData(reviewResponse);
        response.setMessage("Review created successfully");
        return response;
    }

    @GetMapping("public/booking-reviews/field/{fieldId}")
    public ApiResponse<List<BookingReviewResponse>> getFieldReviews(
            @PathVariable Long fieldId
    ) {
        ApiResponse<List<BookingReviewResponse>> response = new ApiResponse<>();
        List<BookingReviewResponse> reviews = bookingReviewService.getFieldReviews(fieldId);
        response.setData(reviews);
        response.setMessage("Field reviews retrieved successfully");
        return response;
    }

    @GetMapping("/user/booking-reviews/my-reviews")
    public ApiResponse<List<BookingReviewResponse>> getMyReviews(@AuthenticationPrincipal Jwt jwt) {
        ApiResponse<List<BookingReviewResponse>> response = new ApiResponse<>();
        Long userId = Long.valueOf(jwt.getSubject());
        List<BookingReviewResponse> reviews = bookingReviewService.getMyReviews(userId);
        response.setData(reviews);
        response.setMessage("My reviews retrieved successfully");
        return response;
    }

}
