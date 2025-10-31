package vn.phankyanh.shopbongda.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String userName;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}
