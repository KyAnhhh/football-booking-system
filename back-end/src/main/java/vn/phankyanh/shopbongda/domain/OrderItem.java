package vn.phankyanh.shopbongda.domain;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") //nhận dện đối tượng đã đc serialize và tránh việc lặp vô hạn khi có quan hệ 2 chiều
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // bỏ qua các thuộc tính proxy do Hibernate thêm vào
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Min(1)
    private Integer quantity;

    @ColumnDefault("0")
    @Min(value = 0, message = "Giảm giá không được âm")
    @Max(value = 100, message = "Giảm giá tối đa là 100%")
    private Integer discount;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Price phải lớn hơn 0")
    private BigDecimal price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
