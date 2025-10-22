package vn.phankyanh.shopbongda.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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

    @Column(nullable = false)
    @Min(value = 0, message = "total_amount không được âm")
    private Integer total_amount;

    @NotBlank(message = "method không được để trống")
    private String method;

    @Column(nullable = false)
    private String transaction_code;

    @Column(nullable = false)
    private LocalDateTime payment_date;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Order> orders = new ArrayList<>(); // Khởi tạo để tránh null
    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    private List<Order> orders ;

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