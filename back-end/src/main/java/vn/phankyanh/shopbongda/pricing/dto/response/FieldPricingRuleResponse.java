package vn.phankyanh.shopbongda.pricing.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class FieldPricingRuleResponse {

    private Long id;

    private Long fieldId;

    private String fieldName;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal pricePerSlot;

    private Integer slotDuration;

    private Boolean isActive;
}
