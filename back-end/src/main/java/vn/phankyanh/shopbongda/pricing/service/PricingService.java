package vn.phankyanh.shopbongda.pricing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PricingService {
    BigDecimal calculatePrice(
            Long fieldId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    BigDecimal calculateSlotPrice(Long fieldId, LocalDateTime slotStartTime);

    BigDecimal calculatePriceForSlots(Long fieldId, List<LocalDateTime> slotStartTimes);

    List<LocalDateTime> generateSlotStartTimes(LocalDateTime startTime, LocalDateTime endTime, int slotDurationMinutes);
}
