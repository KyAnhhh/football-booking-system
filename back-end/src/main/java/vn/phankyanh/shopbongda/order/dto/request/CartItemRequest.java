package vn.phankyanh.shopbongda.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull
    private Long productVariantId;

    @NotNull
    private Long sizeId;

    @Min(1)
    @NotNull
    private Integer quantity;
}
