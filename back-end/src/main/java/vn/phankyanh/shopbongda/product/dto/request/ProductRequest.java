package vn.phankyanh.shopbongda.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String description;

    @NotNull(message = "Giảm giá không được để trống")
    private Integer discount;

    @NotNull(message = "Category không được để trống")
    private Long categoryId;

    @NotNull(message = "Category không được để trống")
    private Long brandId;

}
