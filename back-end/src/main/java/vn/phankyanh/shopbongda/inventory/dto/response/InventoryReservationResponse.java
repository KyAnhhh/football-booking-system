package vn.phankyanh.shopbongda.inventory.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReferenceType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservationStatus;

@Data
@Builder
public class InventoryReservationResponse {
    private Long id;
    private Long inventoryItemId;
    private InventoryReferenceType referenceType;
    private Long referenceId;
    private Long referenceLineId;
    private Integer quantity;
    private InventoryReservationStatus status;
    private Integer quantityOnHand;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime committedAt;
    private LocalDateTime releasedAt;
}
