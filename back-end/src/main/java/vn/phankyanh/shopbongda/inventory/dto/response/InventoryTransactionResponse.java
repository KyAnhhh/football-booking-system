package vn.phankyanh.shopbongda.inventory.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReferenceType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryTransactionType;

@Data
@Builder
public class InventoryTransactionResponse {
    private Long id;
    private Long inventoryItemId;
    private String itemCode;
    private String itemName;
    private InventoryTransactionType transactionType;
    private Integer quantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private Integer beforeReservedQuantity;
    private Integer afterReservedQuantity;
    private InventoryReferenceType referenceType;
    private Long referenceId;
    private String note;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
}
