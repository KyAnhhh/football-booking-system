package vn.phankyanh.shopbongda.pricing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HolidayPricingRuleResponse {

    private Long id;

    private Long fieldId;

    private String fieldName;

    private Long holidayId;

    private String holidayName;

    private LocalDate holidayDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal pricePerSlot;

    private Integer slotDuration;

    private Boolean isActive;
}
