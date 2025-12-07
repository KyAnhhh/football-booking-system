package vn.phankyanh.shopbongda.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer discount;
    private String imageUrl;
    private Integer soldQuantity;
    private Integer dimension;
    private String categoryName;
    private String brandName;
    private String categoryId;
    private String brandId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
