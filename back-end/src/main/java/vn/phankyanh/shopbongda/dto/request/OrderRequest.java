package vn.phankyanh.shopbongda.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long userId;
    private Long paymentId;
    private Long shippingAddressId;
    private BigDecimal totalAmount;
    private String status;
    private Integer shippingCost;
    private LocalDateTime deliveryDate;
    private List<OrderItemRequest> items;
}
