package vn.phankyanh.shopbongda.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") //nhận dện đối tượng đã đc serialize và tránh việc lặp vô hạn khi có quan hệ 2 chiều
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // bỏ qua các thuộc tính proxy do Hibernate thêm vào
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private ShippingAddress shippingAddress;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", message = "Stock không được âm")
    private BigDecimal total_amount;

    @NotBlank(message = "status không được để trống")
    private String status;

    @NotNull(message = "shipping_cost không được để trống")
    @Min(value = 0, message = "shipping_cost phải >= 0")
    private Integer shippingCost;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "delivery_date", nullable = true)
    private LocalDateTime deliveryDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

}
