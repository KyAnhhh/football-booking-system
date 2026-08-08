package vn.phankyanh.shopbongda.review.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminReviewResponse {
    private Long id;
    private String reviewType;
    private Long sourceId;
    private String subjectName;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
