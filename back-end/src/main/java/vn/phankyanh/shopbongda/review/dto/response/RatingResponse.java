package vn.phankyanh.shopbongda.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RatingResponse {

    private Double averageRating;
    private Long totalReviews;

}
