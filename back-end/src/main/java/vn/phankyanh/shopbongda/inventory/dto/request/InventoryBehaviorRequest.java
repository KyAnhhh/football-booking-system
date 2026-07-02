package vn.phankyanh.shopbongda.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryBehavior;

@Data
public class InventoryBehaviorRequest {

    @NotNull(message = "Hành vi tồn kho không được để trống")
    private InventoryBehavior inventoryBehavior;
}
