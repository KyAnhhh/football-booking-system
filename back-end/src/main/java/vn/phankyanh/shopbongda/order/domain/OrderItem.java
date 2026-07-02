package vn.phankyanh.shopbongda.order.domain;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.review.domain.Review;

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
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY)
    private List<Review> review;

    //snapshot
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String sizeName;

    @Column(nullable = false)
    private String imageUrl;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal finalPrice;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal grossProfit = BigDecimal.ZERO;

    @NotNull
    @Min(1)
    private Integer quantity;

    @Builder.Default
    private Boolean stockDeducted = false;

    @Builder.Default
    private Boolean stockReserved = false;

    @Builder.Default
    private Boolean stockReturned = false;

    private Long inventoryReservationId;

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
