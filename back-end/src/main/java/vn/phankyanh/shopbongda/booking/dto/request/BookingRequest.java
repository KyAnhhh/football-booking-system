package vn.phankyanh.shopbongda.booking.dto.request;

import java.time.LocalDateTime;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.extra.dto.request.BookingExtraServiceRequest;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;

@Getter
@Setter
public class BookingRequest {
    @NotNull
    private Long fieldId;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotBlank
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$")
    private String contactPhone;
    private String note;
    private String voucherCode;
    @NotNull
    private PaymentMethod paymentMethod;
    @Valid
    private List<BookingExtraServiceRequest> services;
}
