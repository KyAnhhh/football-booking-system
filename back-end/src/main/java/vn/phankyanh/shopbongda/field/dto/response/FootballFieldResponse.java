package vn.phankyanh.shopbongda.field.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FootballFieldResponse {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
    private BigDecimal pricePerHour;
    private String status;
    private String address;
    private String description;
}
