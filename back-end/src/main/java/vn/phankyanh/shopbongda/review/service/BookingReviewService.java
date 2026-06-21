package vn.phankyanh.shopbongda.review.service;

import java.util.List;

import vn.phankyanh.shopbongda.review.dto.request.BookingReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewResponse;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewSummaryResponse;

public interface BookingReviewService {
    BookingReviewResponse createReview(
            Long bookingId,
            BookingReviewRequest request,
            Long userId
    );

    List<BookingReviewResponse> getFieldReviews(Long fieldId);

    BookingReviewSummaryResponse getFieldReviewSummary(Long fieldId);

    List<BookingReviewResponse> getMyReviews(Long userId);
}
