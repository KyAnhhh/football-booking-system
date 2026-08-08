package vn.phankyanh.shopbongda.review.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.review.dto.response.AdminReviewResponse;
import vn.phankyanh.shopbongda.review.service.BookingReviewService;
import vn.phankyanh.shopbongda.review.service.ReviewService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {
    private final ReviewService reviewService;
    private final BookingReviewService bookingReviewService;

    @GetMapping
    public ApiResponse<List<AdminReviewResponse>> getAll(
            @RequestParam(required = false) String type
    ) {
        String normalizedType = type == null ? "" : type.trim().toUpperCase();
        List<AdminReviewResponse> reviews = new ArrayList<>();
        if (normalizedType.isEmpty() || "PRODUCT".equals(normalizedType)) {
            reviews.addAll(reviewService.getAdminReviews());
        }
        if (normalizedType.isEmpty() || "BOOKING".equals(normalizedType)) {
            reviews.addAll(bookingReviewService.getAdminReviews());
        }
        reviews.sort(Comparator.comparing(AdminReviewResponse::getCreatedAt).reversed());

        ApiResponse<List<AdminReviewResponse>> response = new ApiResponse<>();
        response.setData(reviews);
        return response;
    }

    @DeleteMapping("/{type}/{reviewId}")
    public ApiResponse<Void> delete(
            @PathVariable String type,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if ("PRODUCT".equalsIgnoreCase(type)) {
            reviewService.deleteReview(reviewId, jwt.getClaim("id"));
        } else if ("BOOKING".equalsIgnoreCase(type)) {
            bookingReviewService.deleteReviewByAdmin(reviewId);
        } else {
            throw new IllegalArgumentException("Review type is invalid");
        }

        return new ApiResponse<>();
    }
}
