package vn.phankyanh.shopbongda.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mỗi CartItem thuộc về 1 Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Mỗi CartItem tương ứng với 1 Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Min(value = 1, message = "Stock không được âm")
    private Integer total_quantity = 1;

    // Giá tại thời điểm thêm vào giỏ (tránh thay đổi nếu product update giá)
    @Column(nullable = false)
    private BigDecimal priceAtAddedTime;

//    @Transient
//    public BigDecimal getItemTotal() {
//        return priceAtAddedTime.multiply(BigDecimal.valueOf(quantity));
//    }
}