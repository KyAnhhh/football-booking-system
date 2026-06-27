package vn.phankyanh.shopbongda.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.InventoryTransactionType;

@Data
public class StockAdjustmentRequest {

    @NotNull(message = "Inventory item không được để trống")
    private Long inventoryItemId;

    @NotNull(message = "Loại điều chỉnh không được để trống")
    private InventoryTransactionType transactionType;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng điều chỉnh phải lớn hơn 0")
    private Integer quantity;

    @NotBlank(message = "Lý do điều chỉnh không được để trống")
    private String note;
}
