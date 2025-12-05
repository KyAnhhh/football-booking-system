package vn.phankyanh.shopbongda.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;              // ID của CartItem
    private Long productId;
    private String productName;
    private Integer totalQuantity;
    private BigDecimal priceAtAddedTime; // Giá khi thêm
    private BigDecimal totalPrice;       // totalQuantity * priceAtAddedTime
}
