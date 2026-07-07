package vn.phankyanh.shopbongda.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import vn.phankyanh.shopbongda.user.domain.User;

@Entity
@Table(name = "refund_requests", uniqueConstraints =
        @UniqueConstraint(name = "uk_refund_payment", columnNames = "payment_id"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RefundRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "booking_id")
    private Long bookingId;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(length = 500)
    private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40)
    private RefundStatus status;
    @Column(name = "bank_code", length = 50)
    private String bankCode;
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;
    @Column(name = "bank_account_name", length = 150)
    private String bankAccountName;
    @Column(name = "refund_transaction_code", length = 100)
    private String refundTransactionCode;
    @Column(name = "admin_note", length = 500)
    private String adminNote;
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @PrePersist void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        requestedAt = requestedAt == null ? now : requestedAt;
        createdAt = now; updatedAt = now;
    }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
