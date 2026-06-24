package vn.phankyanh.shopbongda.voucher.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.user.domain.User;

@Entity
@Table(
        name = "voucher_usages",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_voucher_usage_booking",
                        columnNames = "booking_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", insertable = false, updatable = false)
    private Voucher voucher;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "voucher_code", nullable = false)
    private String voucherCode;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @Column(name = "is_refunded", nullable = false)
    private Boolean isRefunded;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @PrePersist
    protected void onCreate() {
        if (this.usedAt == null) {
            this.usedAt = LocalDateTime.now();
        }

        if (this.isRefunded == null) {
            this.isRefunded = false;
        }
    }
}
