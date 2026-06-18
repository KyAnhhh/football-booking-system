package vn.phankyanh.shopbongda.booking.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.phankyanh.shopbongda.extra.domain.BookingExtraService;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.review.domain.BookingReview;
import vn.phankyanh.shopbongda.user.domain.User;

@Entity
// @Table(name = "bookings")
@Table(
    name = "bookings",
    indexes = {
        @Index(
            name = "idx_booking_field_time",
            columnList = "field_id,start_time,end_time"
        )
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private FootballField field;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private BookingReview bookingReview;

    @OneToMany(
        mappedBy = "booking",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<BookingExtraService> extraServices;

    private BigDecimal extraServicePrice;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "membership_discount")
    private BigDecimal membershipDiscount;

    @Column(name = "voucher_discount")
    private BigDecimal voucherDiscount;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "contact_phone")
    private String contactPhone;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void validateTime() {
        if (startTime != null
                && endTime != null
                && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
