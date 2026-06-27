package vn.phankyanh.shopbongda.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;

@Data
public class InventoryItemRequest {

    @NotBlank(message = "Mã kho không được để trống")
    private String itemCode;

    @NotBlank(message = "Tên item không được để trống")
    private String name;

    @NotNull(message = "Loại item không được để trống")
    private InventoryItemType itemType;

    private Long referenceId;

    private String unit;

    private Boolean active;

    private Integer lowStockThreshold;
}
