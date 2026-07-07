package vn.phankyanh.shopbongda.payment.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class RefundBankAccountRequest {
    @NotBlank @Size(max=50) private String bankCode;
    @NotBlank @Size(max=50) private String bankAccountNumber;
    @NotBlank @Size(max=150) private String bankAccountName;
}
