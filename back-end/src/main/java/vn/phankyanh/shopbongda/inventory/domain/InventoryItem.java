package vn.phankyanh.shopbongda.inventory.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String itemCode;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InventoryBehavior inventoryBehavior = InventoryBehavior.CONSUMABLE;

    private Long referenceId;

    private String unit;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Integer lowStockThreshold = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (active == null) {
            active = true;
        }

        if (lowStockThreshold == null) {
            lowStockThreshold = 0;
        }

        if (inventoryBehavior == null) {
            inventoryBehavior = InventoryBehavior.CONSUMABLE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
