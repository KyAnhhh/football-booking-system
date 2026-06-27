package vn.phankyanh.shopbongda.inventory.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockImportResponse {
    private Long id;
    private String importCode;
    private Long supplierId;
    private String supplierName;
    private BigDecimal totalAmount;
    private String note;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private List<StockImportItemResponse> items;
}
