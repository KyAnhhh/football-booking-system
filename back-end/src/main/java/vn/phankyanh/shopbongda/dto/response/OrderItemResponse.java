package vn.phankyanh.shopbongda.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}

