package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;

    private String type;

    private String status;

    private BigDecimal totalAmount;

    private String method;

    private String transactionCode;

    private LocalDateTime paymentDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
