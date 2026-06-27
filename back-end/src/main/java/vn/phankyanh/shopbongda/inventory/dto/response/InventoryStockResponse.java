package vn.phankyanh.shopbongda.inventory.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;

@Data
@Builder
public class InventoryStockResponse {
    private Long id;
    private Long inventoryItemId;
    private String itemCode;
    private String itemName;
    private InventoryItemType itemType;
    private String unit;
    private Integer lowStockThreshold;
    private Integer quantityOnHand;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Boolean lowStock;
    private LocalDateTime updatedAt;
}
