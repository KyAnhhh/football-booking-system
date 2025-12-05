package vn.phankyanh.shopbongda.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long cartId;
    private Long productId;
    private Integer quantity;
}
