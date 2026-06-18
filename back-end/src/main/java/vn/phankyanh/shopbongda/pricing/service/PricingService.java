package vn.phankyanh.shopbongda.pricing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PricingService {
        BigDecimal calculatePrice(
            Long fieldId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
