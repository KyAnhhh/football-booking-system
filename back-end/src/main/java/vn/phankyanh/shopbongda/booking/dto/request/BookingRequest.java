package vn.phankyanh.shopbongda.booking.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;

@Getter
@Setter
public class BookingRequest {
    private Long fieldId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String contactPhone;
    private String note;
    private PaymentMethod paymentMethod; 
}
