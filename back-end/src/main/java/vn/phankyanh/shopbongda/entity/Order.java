package vn.phankyanh.shopbongda.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private ShippingAddress shippingAddress;

    @Column(nullable = false)
    @Min(value = 0, message = "Stock không được âm")
    private Integer total_amount;

    @NotBlank(message = "status không được để trống")
    private String status;

    @Column(nullable = false)
    @NotBlank(message = "shipping_cost không được để trống")
    private Integer shipping_cost;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime deliveryDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}