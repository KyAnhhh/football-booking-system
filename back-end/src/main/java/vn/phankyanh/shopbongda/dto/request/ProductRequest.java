package vn.phankyanh.shopbongda.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer stock;

    @Min(value = 0)
    @Max(value = 100)
    private Integer discount;

    private String imageUrl;

    private Integer dimension;

    @NotNull(message = "Category không được để trống")
    private Long categoryId;

    @NotNull(message = "Category không được để trống")
    private Long brandId;
}
