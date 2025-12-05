package vn.phankyanh.shopbongda.domain;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "product_id_denorm", nullable = false)
    private Long productId;

    @Column(name = "product_name_denorm", nullable = false)
    private String productName;

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

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Total phải lớn hơn 0")
    private BigDecimal total;

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
