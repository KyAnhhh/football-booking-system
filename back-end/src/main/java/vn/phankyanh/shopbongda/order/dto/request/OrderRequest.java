package vn.phankyanh.shopbongda.order.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull
    private Long shippingAddressId;

    private PaymentMethod paymentMethod;

    @NotNull
    @Size(min = 1, message = "Order phải có ít nhất 1 sản phẩm")
    private List<OrderItemRequest> items;
}
