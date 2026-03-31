package vn.phankyanh.shopbongda.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sizeName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private Integer quantity;
    private BigDecimal total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

