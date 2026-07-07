package vn.phankyanh.shopbongda.payment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import vn.phankyanh.shopbongda.order.domain.Order;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40, columnDefinition = "varchar(40)")
    private PaymentStatus paymentStatus;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "TotalAmount không được âm")
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, unique = true, length = 100)
    private String transactionCode;

    @Column(nullable = true)
    private LocalDateTime paymentDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    @JsonIgnore
    private Order order ;

    // @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    // private Booking booking;

    @PrePersist
    protected void onCreate() {
        this.transactionCode = this.transactionCode != null
                ? this.transactionCode
                : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.paymentMethod = this.paymentMethod != null ? this.paymentMethod : PaymentMethod.COD; // Chỉ set COD nếu chưa có giá trị
        this.paymentStatus = this.paymentStatus != null ? this.paymentStatus : PaymentStatus.UNPAID;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
