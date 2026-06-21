package vn.phankyanh.shopbongda.review.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingReviewSummaryResponse {
    private Long fieldId;
    private Double averageRating;
    private Long totalReviews;
    private Long fiveStarCount;
    private Long fourStarCount;
    private Long threeStarCount;
    private Long twoStarCount;
    private Long oneStarCount;
}
