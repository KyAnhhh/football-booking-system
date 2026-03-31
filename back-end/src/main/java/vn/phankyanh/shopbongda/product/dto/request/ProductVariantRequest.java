package vn.phankyanh.shopbongda.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVariantRequest {

    @NotNull(message = "ProductId không được để trống")
    private Long productId;

    @NotNull(message = "SizeId không được để trống")
    private Long sizeId;

    @NotNull(message = "Price không được để trống")
    @Min(value = 0, message = "Price phải >= 0")
    private BigDecimal price;

    @NotNull(message = "Stock không được để trống")
    @Min(value = 0, message = "Stock phải >= 0")
    private Integer stock;
}
