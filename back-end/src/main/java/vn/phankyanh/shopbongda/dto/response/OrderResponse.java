package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private Integer shippingCost;
    private String status;
    private String fullName;
    private String phone;
    private String address;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}
