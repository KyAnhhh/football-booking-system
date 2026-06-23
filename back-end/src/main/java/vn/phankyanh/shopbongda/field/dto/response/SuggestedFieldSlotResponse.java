package vn.phankyanh.shopbongda.field.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.field.domain.SuggestionType;

@Getter
@Setter
@Builder
public class SuggestedFieldSlotResponse {
    private SuggestionType type;
    private Long fieldId;
    private String fieldName;
    private String fieldType;
    private String imageUrl;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal price;
    private Integer score;
    private String reason;
}
