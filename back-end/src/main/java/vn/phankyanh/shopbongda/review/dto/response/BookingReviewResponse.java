package vn.phankyanh.shopbongda.review.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingReviewResponse {
    private Long id;
    private String userName;
    private Integer rating;
    private String comment;
    private String fieldName;
    private LocalDateTime createdAt;
}
