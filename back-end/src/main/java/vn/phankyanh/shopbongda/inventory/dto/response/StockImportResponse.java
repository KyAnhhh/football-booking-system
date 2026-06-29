package vn.phankyanh.shopbongda.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import vn.phankyanh.shopbongda.inventory.domain.StockImportStatus;

@Data
@Builder
public class StockImportResponse {
    private Long id;
    private String importCode;
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalAmount;
    private StockImportStatus status;
    private String note;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime postedAt;
    private LocalDateTime cancelledAt;
    private List<StockImportItemResponse> items;
}
