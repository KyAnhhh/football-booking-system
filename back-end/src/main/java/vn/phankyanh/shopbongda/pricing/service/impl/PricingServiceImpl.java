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
import java.util.Optional;

import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.domain.HolidayPricingRule;
import vn.phankyanh.shopbongda.pricing.repository.FieldPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.repository.HolidayPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.service.PricingService;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private static final int DEFAULT_SLOT_DURATION = 90;

    private final FieldPricingRuleRepository fieldPricingRuleRepository;
    private final HolidayPricingRuleRepository holidayPricingRuleRepository;

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

            SlotPricing matchedRule =
                    findSlotPricingForTime(
                            fieldId,
                            current
                    );

            totalPrice = totalPrice.add(
                    matchedRule.pricePerSlot()
            );

            current = current.plusMinutes(
                    matchedRule.slotDuration()
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

        return findSlotPricingForTime(
                fieldId,
                slotStartTime
        ).pricePerSlot();
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

    private SlotPricing findSlotPricingForTime(
            Long fieldId,
            LocalDateTime dateTime
    ) {

        LocalTime bookingTime =
                dateTime.toLocalTime();

        Optional<HolidayPricingRule> holidayRule =
                holidayPricingRuleRepository.findFirstApplicableRule(
                        fieldId,
                        dateTime.toLocalDate(),
                        bookingTime
                );

        if (holidayRule.isPresent()) {
            HolidayPricingRule rule = holidayRule.get();
            return new SlotPricing(
                    rule.getPricePerSlot(),
                    getSlotDuration(rule.getSlotDuration())
            );
        }

        DayOfWeek dayOfWeek =
                dateTime.getDayOfWeek();

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
                .map(rule ->
                        new SlotPricing(
                                rule.getPricePerSlot(),
                                getSlotDuration(rule.getSlotDuration())
                        )
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pricing rule not found for booking time"
                        ));
    }

    private int getSlotDuration(Integer slotDuration) {
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

    private record SlotPricing(
            BigDecimal pricePerSlot,
            int slotDuration
    ) {
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
