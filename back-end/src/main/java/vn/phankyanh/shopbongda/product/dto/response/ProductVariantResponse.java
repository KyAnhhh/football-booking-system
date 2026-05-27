package vn.phankyanh.shopbongda.product.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    private Long id;
    private Long productId;
    private Long sizeId;
    private String sizeCode;
    private BigDecimal price;
    private Integer stock;
    private Integer soldQuantity;
}
