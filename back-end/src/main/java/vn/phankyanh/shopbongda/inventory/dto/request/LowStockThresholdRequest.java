package vn.phankyanh.shopbongda.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LowStockThresholdRequest {

    @NotNull(message = "Ngưỡng cảnh báo không được để trống")
    @Min(value = 0, message = "Ngưỡng cảnh báo không được âm")
    private Integer lowStockThreshold;
}
