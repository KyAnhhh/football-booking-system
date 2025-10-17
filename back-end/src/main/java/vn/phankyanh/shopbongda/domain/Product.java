package vn.phankyanh.shopbongda.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(length = 1000, nullable = true, unique = true)
    private String imageUrl;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist 
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now(); // reset time = now when update
    }

    @PreUpdate // update timestamp
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
