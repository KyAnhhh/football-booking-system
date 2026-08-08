package vn.phankyanh.shopbongda.review.service;

import java.util.List;

import vn.phankyanh.shopbongda.review.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.review.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.review.dto.response.AdminReviewResponse;

public interface ReviewService {

    List<ReviewResponse> getReviewsByProductId(Long productId);

    RatingResponse getAverageRating(Long productId);

    ReviewResponse createReview(
            Long orderItemId,
            ReviewRequest request,
            Long userId
    );

    boolean canUserReview(Long userId, Long orderItemId);

    void deleteReview(Long reviewId, Long userId);

    List<AdminReviewResponse> getAdminReviews();
}
