package vn.phankyanh.shopbongda.inventory.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVariantSyncIssueResponse {
    private Long variantId;
    private String productName;
    private String sizeCode;
    private String itemCode;
    private String message;
}
