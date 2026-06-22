package vn.phankyanh.shopbongda.booking.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingStatusHistoryResponse {
    private Long id;
    private Long bookingId;
    private String oldStatus;
    private String newStatus;
    private Long changedById;
    private String changedByName;
    private String changedByRole;
    private String note;
    private LocalDateTime createdAt;
}
