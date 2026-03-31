package vn.phankyanh.shopbongda.order.dto.response;

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
    private String sizeCode;
    private String productThumbnail;
    private Integer totalQuantity;
    private BigDecimal priceAtAddedTime; // Giá khi thêm
    private BigDecimal totalPrice;       // totalQuantity * 
    private Integer discount;
}
