package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id;              // ID của CartItem
    private Long productId;
    private String productName;
    private Integer totalQuantity;
    private BigDecimal priceAtAddedTime; // Giá khi thêm
    private BigDecimal totalPrice;       // totalQuantity * priceAtAddedTime
}
