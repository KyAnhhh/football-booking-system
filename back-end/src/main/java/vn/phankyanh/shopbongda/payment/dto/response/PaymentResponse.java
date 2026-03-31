package vn.phankyanh.shopbongda.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import vn.phankyanh.shopbongda.order.domain.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;

    private String paymentStatus;

    private String paymentMethod;

    private BigDecimal totalAmount;

    private LocalDateTime paymentDate;

    private Long orderId;

    private String transactionCode;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
