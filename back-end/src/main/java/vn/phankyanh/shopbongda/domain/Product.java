package vn.phankyanh.shopbongda.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150, unique = true)
    @NotBlank(message = "Name không được để trống")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Price phải lớn hơn 0")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "Stock không được âm")
    private Integer stock;

    @ColumnDefault("0")
    @Min(value = 0, message = "Giảm giá không được âm")
    @Max(value = 100, message = "Giảm giá tối đa là 100%")
    private Integer discount;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private Integer sold_quantity = 0;

    @Column(nullable = false)
    private Integer dimension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // onetomany là 1 product có nhiều cart items
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartItem> cartItem;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> review;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<WishList> wishLists;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate // update timestamp
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    @Transient
//    public BigDecimal getDiscountedPrice() {
//        if (discount == null || discount == 0) {
//            return price;
//        }
//        BigDecimal discountAmount = price.multiply(BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100)));
//        return price.subtract(discountAmount);
//    }
}
