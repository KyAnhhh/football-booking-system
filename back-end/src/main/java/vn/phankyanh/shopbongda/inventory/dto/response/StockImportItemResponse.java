package vn.phankyanh.shopbongda.inventory.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockImportItemResponse {
    private Long id;
    private Long inventoryItemId;
    private String itemCode;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
}
