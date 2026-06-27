package vn.phankyanh.shopbongda.inventory.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;

@Data
@Builder
public class InventoryItemResponse {
    private Long id;
    private String itemCode;
    private String name;
    private InventoryItemType itemType;
    private Long referenceId;
    private String unit;
    private Boolean active;
    private Integer lowStockThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
