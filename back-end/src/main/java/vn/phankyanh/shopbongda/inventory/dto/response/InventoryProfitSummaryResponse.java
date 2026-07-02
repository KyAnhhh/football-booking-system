package vn.phankyanh.shopbongda.inventory.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryProfitSummaryResponse {
    private BigDecimal productRevenue;
    private BigDecimal productCost;
    private BigDecimal productGrossProfit;
    private BigDecimal extraRevenue;
    private BigDecimal extraCost;
    private BigDecimal extraGrossProfit;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalGrossProfit;
}
