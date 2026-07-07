package vn.phankyanh.shopbongda.payment.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class CompleteRefundRequest {
    @NotBlank @Size(max=100) private String refundTransactionCode;
    @Size(max=500) private String adminNote;
}
