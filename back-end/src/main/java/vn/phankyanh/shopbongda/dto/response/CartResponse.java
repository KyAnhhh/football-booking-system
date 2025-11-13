package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CartResponse {

    private Long id;           // ID của cartItem
    private Long userId;
    private Integer totalAmount;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
