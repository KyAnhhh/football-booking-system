package vn.phankyanh.shopbongda.payment.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import vn.phankyanh.shopbongda.payment.domain.RefundStatus;
@Getter @Builder
public class RefundResponse {
    private Long id, paymentId, orderId, bookingId, userId;
    private String username, reason, bankCode, bankAccountNumber, bankAccountName;
    private String refundTransactionCode, adminNote, failureReason;
    private BigDecimal amount;
    private RefundStatus status;
    private LocalDateTime requestedAt, processedAt;
}
