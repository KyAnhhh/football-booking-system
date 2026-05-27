package vn.phankyanh.shopbongda.product.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductUpdateRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer discount;
    private Long categoryId;
    private Long brandId;

    private String variants;
}
