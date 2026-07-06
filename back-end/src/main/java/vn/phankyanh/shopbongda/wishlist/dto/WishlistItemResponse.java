package vn.phankyanh.shopbongda.wishlist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal minimumPrice;
    private Integer discount;
    private LocalDateTime createdAt;
}
