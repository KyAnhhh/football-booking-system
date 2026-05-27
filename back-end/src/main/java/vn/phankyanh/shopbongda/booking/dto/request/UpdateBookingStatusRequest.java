package vn.phankyanh.shopbongda.booking.dto.request;

import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;

@Getter
@Setter
public class UpdateBookingStatusRequest {
    private BookingStatus bookingStatus;
}
