package vn.phankyanh.shopbongda.review.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.review.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.review.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.review.service.ReviewService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

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
    // GET /api/public/products/{productId}/average-rating
    @GetMapping("/public/products/{productId}/average-rating")
    public ApiResponse<RatingResponse> getAverageRating(@PathVariable("productId") Long productId) {
        ApiResponse<RatingResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.getAverageRating(productId));
        return apiResponse;
    }
    
    // GET /api/user/order-item/{orderItemId}/can-review
    @GetMapping("/user/order-items/{orderItemId}/can-review")
    public ApiResponse<Boolean> canUserReviewProduct(@PathVariable("orderItemId") Long orderItemId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.canUserReview(userId, orderItemId));
        return apiResponse;
    }
    
    // POST /api/user/order-items/{orderItemId}/reviews
    @PostMapping("/user/order-items/{orderItemId}/reviews")
    public ApiResponse<ReviewResponse> createReview(@PathVariable("orderItemId") Long orderItemId, @RequestBody ReviewRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(reviewService.createReview(orderItemId, request, userId));
        return apiResponse;
    }

    // DELETE /api/admin/reviews/{reviewId}
    @DeleteMapping("/admin/reviews/{reviewId}")
    public ApiResponse<String> deleteReview(@PathVariable("reviewId") Long reviewId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        this.reviewService.deleteReview(reviewId, userId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Deleted review successfully");
        return response;
    }
}
