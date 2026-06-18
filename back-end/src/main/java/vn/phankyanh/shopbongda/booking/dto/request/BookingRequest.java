package vn.phankyanh.shopbongda.booking.dto.request;

import java.time.LocalDateTime;

import java.util.List;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.extra.dto.request.BookingExtraServiceRequest;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;

@Getter
@Setter
public class BookingRequest {
    private Long fieldId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String contactPhone;
    private String note;
    private String voucherCode;
    private PaymentMethod paymentMethod; 
    @Valid
    private List<BookingExtraServiceRequest> services;
}
