package vn.phankyanh.shopbongda.inventory.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockImportRequest {

    private String importCode;

    @NotNull(message = "Nhà cung cấp không được để trống")
    private Long supplierId;

    private String note;

    @Valid
    @NotEmpty(message = "Phiếu nhập phải có ít nhất một item")
    private List<StockImportItemRequest> items;
}
