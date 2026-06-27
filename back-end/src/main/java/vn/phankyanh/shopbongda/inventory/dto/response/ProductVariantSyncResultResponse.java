package vn.phankyanh.shopbongda.inventory.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVariantSyncResultResponse {
    private long totalRows;
    private long successRows;
    private long skippedRows;
    private long failedRows;
    private List<Long> createdInventoryItemIds;
    private List<ProductVariantSyncIssueResponse> errors;
}
