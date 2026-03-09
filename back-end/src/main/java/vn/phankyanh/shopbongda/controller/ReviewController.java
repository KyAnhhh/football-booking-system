package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;
    // GET /api/public/products/{productId}/reviews
    @GetMapping("/public/products/{productId}/reviews")
    public ApiResponse<List<ReviewResponse>> getReviewsByProductId(@PathVariable("productId") Long productId) {
        ApiResponse<List<ReviewResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.getReviewsByProductId(productId));
        return apiResponse;
    }
    // GET /api/public/products/{productId}/rating
    @GetMapping("/public/products/{productId}/rating")
    public ApiResponse<RatingResponse> getAverageRating(@PathVariable("productId") Long productId) {
        ApiResponse<RatingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.getAverageRating(productId));
        return apiResponse;
    }
    // GET /api/public/user/products/{productId}/can-review
    @GetMapping("/public/user/products/{productId}/can-review")
    public ApiResponse<Boolean> canUserReviewProduct(@PathVariable("productId") Long productId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("user_id");
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.canUserReviewProduct(userId, productId));
        return apiResponse;
    }
    // POST /api/public/user/products/{productId}/reviews
    @PostMapping("/public/user/products/{productId}/reviews")
    public ApiResponse<ReviewResponse> createReview(@PathVariable("productId") Long productId, @AuthenticationPrincipal Jwt jwt, ReviewRequest request) {
        Long userId = jwt.getClaim("user_id");
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.createReview(request, userId, productId));
        return apiResponse;
    }
    // DELETE /api/admin/reviews/{reviewId}
    @DeleteMapping("/admin/reviews/{reviewId}")
    public void deleteReview(@PathVariable("reviewId") Long reviewId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("user_id");
        reviewService.deleteReview(reviewId, userId);
    }
}
