package vn.phankyanh.shopbongda.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.phankyanh.shopbongda.domain.PaymentMethod;

@Data
public class CheckoutRequest {
    @NotEmpty(message = "Phải chọn ít nhất 1 sản phẩm")
    private List<Long> cartItemIds;
    
    @NotNull(message = "Địa chỉ giao hàng không được để trống")
    private Long shippingAddressId;
    
    private PaymentMethod paymentMethod;
}
