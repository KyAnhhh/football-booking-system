package vn.phankyanh.shopbongda.booking.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {
    private Long fieldId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
