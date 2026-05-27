package vn.phankyanh.shopbongda.payment.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private Long orderId;

    private String type;

    private String status;

    private BigDecimal totalAmount;

    private String method;

    private String transactionCode;
}
