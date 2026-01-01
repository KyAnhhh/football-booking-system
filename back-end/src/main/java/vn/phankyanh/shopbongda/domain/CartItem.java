package vn.phankyanh.shopbongda.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_variant_id", "size_id"}) // ràng buộc khi add song song => có thể tạo 2 cartItem trùng
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Column(name = "product_id_denorm", nullable = false)
    private Long productId;
    
    @Column(name = "product_name_denorm", nullable = false)
    private String productName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Column(name = "size_code_denorm", nullable = false)
    private String sizeCode;

    @Column(name = "product_thumbnail", nullable = false)
    private String productThumbnail;

    @Column(nullable = false)
    @Min(value = 1)
    private Integer totalQuantity;

    @Column(nullable = false)
    private BigDecimal priceAtAddedTime;

}