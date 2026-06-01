package vn.phankyanh.shopbongda.review.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingReviewResponse {
    private Long id;
    private Long bookingId;
    private String fieldName;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
