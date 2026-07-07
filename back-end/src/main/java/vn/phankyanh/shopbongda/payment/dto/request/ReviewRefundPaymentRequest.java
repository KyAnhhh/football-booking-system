package vn.phankyanh.shopbongda.payment.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ReviewRefundPaymentRequest {
    @NotNull private Boolean paymentReceived;
}
