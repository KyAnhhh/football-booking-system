package vn.phankyanh.shopbongda.dto.request;

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
    private Integer quantity;
}
