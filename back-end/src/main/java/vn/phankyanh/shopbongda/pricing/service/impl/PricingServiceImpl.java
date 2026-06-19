package vn.phankyanh.shopbongda.pricing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.repository.FieldPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.service.PricingService;
import vn.phankyanh.shopbongda.holiday.service.HolidayService;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private static final int DEFAULT_SLOT_DURATION = 90;

    private final FieldPricingRuleRepository fieldPricingRuleRepository;
    private final HolidayService holidayService;

    @Override
    public BigDecimal calculatePrice(
            Long fieldId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        validateBookingTime(startTime, endTime);

        BigDecimal totalPrice = BigDecimal.ZERO;

        LocalDateTime current = startTime;

        while (current.isBefore(endTime)) {

            FieldPricingRule matchedRule =
                    findRuleForTime(
                            fieldId,
                            current
                    );

            totalPrice = totalPrice.add(
                    matchedRule.getPricePerSlot()
            );

            current = current.plusMinutes(
                    getSlotDuration(matchedRule)
            );
        }

        return totalPrice;
    }

    @Override
    public BigDecimal calculateSlotPrice(
            Long fieldId,
            LocalDateTime slotStartTime
    ) {

        validateSlotTime(slotStartTime);

        return findRuleForTime(
                fieldId,
                slotStartTime
        ).getPricePerSlot();
    }

    @Override
    public BigDecimal calculatePriceForSlots(
            Long fieldId,
            List<LocalDateTime> slotStartTimes
    ) {

        if (slotStartTimes == null || slotStartTimes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (LocalDateTime slotStartTime : slotStartTimes) {
            totalPrice = totalPrice.add(
                    calculateSlotPrice(
                            fieldId,
                            slotStartTime
                    )
            );
        }

        return totalPrice;
    }

    @Override
    public List<LocalDateTime> generateSlotStartTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int slotDurationMinutes
    ) {

        validateBookingTime(startTime, endTime);

        if (slotDurationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Slot duration must be greater than 0"
            );
        }

        List<LocalDateTime> slotStartTimes =
                new ArrayList<>();

        LocalDateTime current = startTime;

        while (current.isBefore(endTime)) {
            slotStartTimes.add(current);
            current = current.plusMinutes(slotDurationMinutes);
        }

        return slotStartTimes;
    }

    private FieldPricingRule findRuleForTime(
            Long fieldId,
            LocalDateTime dateTime
    ) {

        DayOfWeek dayOfWeek =
                resolvePricingDay(dateTime);

        LocalTime bookingTime =
                dateTime.toLocalTime();

        List<FieldPricingRule> rules =
                fieldPricingRuleRepository
                        .findByFieldIdAndDayOfWeekAndIsActiveTrue(
                                fieldId,
                                dayOfWeek
                        );

        return rules.stream()
                .filter(rule ->
                        !rule.getStartTime().isAfter(bookingTime)
                                && rule.getEndTime().isAfter(bookingTime)
                )
                .max(
                        Comparator.comparing(
                                FieldPricingRule::getStartTime
                        )
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pricing rule not found for booking time"
                        ));
    }

    private DayOfWeek resolvePricingDay(
            LocalDateTime dateTime
    ) {

        if (holidayService.isHoliday(dateTime.toLocalDate())) {
            return DayOfWeek.SUNDAY;
        }

        return dateTime.getDayOfWeek();
    }

    private int getSlotDuration(
            FieldPricingRule rule
    ) {

        Integer slotDuration =
                rule.getSlotDuration();

        if (slotDuration == null) {
            return DEFAULT_SLOT_DURATION;
        }

        if (slotDuration <= 0) {
            throw new IllegalArgumentException(
                    "Invalid pricing rule slot duration"
            );
        }

        return slotDuration;
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

    private void validateSlotTime(
            LocalDateTime slotStartTime
    ) {

        if (slotStartTime == null) {
            throw new IllegalArgumentException(
                    "Slot start time cannot be null"
            );
        }
    }
}
