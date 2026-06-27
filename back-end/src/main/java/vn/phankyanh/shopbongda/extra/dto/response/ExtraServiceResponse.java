package vn.phankyanh.shopbongda.extra.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ExtraServiceResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean isActive;

    private Boolean trackStock;

    private Integer stockQuantity;

    private Integer remainingQuantity;

    private String unit;

    private Long inventoryItemId;
}
