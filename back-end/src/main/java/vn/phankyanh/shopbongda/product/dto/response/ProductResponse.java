package vn.phankyanh.shopbongda.product.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Integer discount;

    private Long categoryId;
    private String categoryName;

    private Long brandId;
    private String brandName;

    private List<ProductVariantResponse> variants;
    private List<String> imageUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
