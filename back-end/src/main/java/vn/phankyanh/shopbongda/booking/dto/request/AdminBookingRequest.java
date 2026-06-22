package vn.phankyanh.shopbongda.booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBookingRequest extends BookingRequest {
    private Long userId;
    private Boolean confirmImmediately;
}
