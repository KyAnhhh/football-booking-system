package vn.phankyanh.shopbongda.order.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String userId;

    private String fullName;
    private String phone;
    private String address;

    private BigDecimal shippingCost;
    private BigDecimal totalAmount;

    private String orderStatus;

    private String paymentStatus;

    private String paymentMethod;

    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    private List<OrderItemResponse> items;
}
