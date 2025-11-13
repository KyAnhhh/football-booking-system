package vn.phankyanh.shopbongda.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import java.util.UUID;

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

    @Column(nullable = false, length = 100)
    private String status;

    @DecimalMin(value = "0.0", inclusive = true, message = "TotalAmount không được âm")
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Hiện tại chỉ hỗ trợ thanh toán tiền mặt khi giao hàng
    // Sau này có thể mở rộng thêm các phương thức khác (MOMO, chuyển khoản, v.v.)
    @Column(nullable = false, length = 50)
    private String method;

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
        this.method = "Tiền mặt"; // Thanh toán khi nhận hàng
        this.status = "Chưa thanh toán";
        this.paymentDate = null; // vì chưa thanh toán
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
