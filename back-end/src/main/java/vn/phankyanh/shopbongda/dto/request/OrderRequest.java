package vn.phankyanh.shopbongda.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull
    private Long shippingAddressId;

    // @DecimalMin("0.0")
    // private BigDecimal shippingCost;

    private String paymentMethod;

    @NotNull
    @Size(min = 1, message = "Order phải có ít nhất 1 sản phẩm")
    private List<OrderItemRequest> items;
}
