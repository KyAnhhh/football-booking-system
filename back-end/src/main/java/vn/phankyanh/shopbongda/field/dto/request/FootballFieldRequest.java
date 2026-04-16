package vn.phankyanh.shopbongda.field.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.field.domain.FieldType;

@Getter
@Setter
public class FootballFieldRequest {
    private String name;
    private FieldType type;
    private String imageUrl;
    private BigDecimal pricePerHour;
    private String address;
    private String description;
}
