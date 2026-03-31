package vn.phankyanh.shopbongda.payment.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") //nhận dện đối tượng đã đc serialize và tránh việc lặp vô hạn khi có quan hệ 2 chiều
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

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
    private Order order ;

    @PrePersist
    protected void onCreate() {
        this.transactionCode = this.transactionCode != null
                ? this.transactionCode
                : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type = "Giao hàng tận nơi";
        this.paymentMethod = this.paymentMethod != null ? this.paymentMethod : PaymentMethod.COD; // Chỉ set COD nếu chưa có giá trị
        this.paymentStatus = PaymentStatus.UNPAID;
        this.paymentDate = this.paymentMethod == PaymentMethod.COD ? null : this.paymentDate; // vì chưa thanh toán
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
