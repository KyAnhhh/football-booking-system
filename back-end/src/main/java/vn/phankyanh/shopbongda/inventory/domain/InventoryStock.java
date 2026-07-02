package vn.phankyanh.shopbongda.inventory.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_stocks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false, unique = true)
    private InventoryItem inventoryItem;

    @Column(nullable = false)
    private Integer quantityOnHand;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal averageUnitCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal inventoryValue = BigDecimal.ZERO;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void normalize() {
        if (quantityOnHand == null) {
            quantityOnHand = 0;
        }

        if (reservedQuantity == null) {
            reservedQuantity = 0;
        }

        availableQuantity = Math.max(quantityOnHand - reservedQuantity, 0);
        if (averageUnitCost == null) {
            averageUnitCost = BigDecimal.ZERO;
        }
        if (inventoryValue == null) {
            inventoryValue = BigDecimal.ZERO;
        }
        updatedAt = LocalDateTime.now();
    }
}
