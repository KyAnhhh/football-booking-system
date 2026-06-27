package vn.phankyanh.shopbongda.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVariantSyncPreviewResponse {
    private long totalVariants;
    private long linkedVariants;
    private long unlinkedVariants;
    private long conflictVariants;
}
