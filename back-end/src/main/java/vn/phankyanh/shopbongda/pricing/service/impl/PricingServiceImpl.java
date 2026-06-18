package vn.phankyanh.shopbongda.pricing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.repository.FieldPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.service.PricingService;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final FieldPricingRuleRepository fieldPricingRuleRepository;

    @Override
    public BigDecimal calculatePrice(
            Long fieldId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        validateBookingTime(startTime, endTime);

        DayOfWeek dayOfWeek =
                startTime.getDayOfWeek();

        LocalTime bookingTime =
                startTime.toLocalTime();

        FieldPricingRule matchedRule =
                fieldPricingRuleRepository
                        .findFirstByFieldIdAndDayOfWeekAndIsActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanOrderByStartTimeDesc(
                                fieldId,
                                dayOfWeek,
                                bookingTime,
                                bookingTime
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pricing rule not found for booking time"
                                ));

        long minutes =
                Duration.between(
                        startTime,
                        endTime
                ).toMinutes();

        BigDecimal hours =
                BigDecimal.valueOf(minutes)
                        .divide(
                                BigDecimal.valueOf(60),
                                2,
                                RoundingMode.HALF_UP
                        );

        return matchedRule
                .getPricePerHour()
                .multiply(hours);
    }

    private void validateBookingTime(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Start time and end time cannot be null"
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }
    }
}
