package vn.phankyanh.shopbongda.pricing.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class FieldPricingRuleRequest {

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Price per slot is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per slot must be greater than 0")
    private BigDecimal pricePerSlot;

    @Min(value = 1, message = "Slot duration must be at least 1 minute")
    private Integer slotDuration;
}
