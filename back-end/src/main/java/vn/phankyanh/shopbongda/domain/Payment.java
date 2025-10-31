package vn.phankyanh.shopbongda.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "type không được để trống")
    private String type;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "status không được để trống")
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "total_amount không được âm")
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @NotBlank(message = "method không được để trống")
    private String method;

    @Column(nullable = false, unique = true, length = 100)
    private String transactionCode;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Order order ;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
