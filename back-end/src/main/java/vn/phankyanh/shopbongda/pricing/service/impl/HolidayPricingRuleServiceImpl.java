package vn.phankyanh.shopbongda.pricing.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.holiday.domain.Holiday;
import vn.phankyanh.shopbongda.holiday.repository.HolidayRepository;
import vn.phankyanh.shopbongda.pricing.domain.HolidayPricingRule;
import vn.phankyanh.shopbongda.pricing.dto.request.HolidayPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.HolidayPricingRuleResponse;
import vn.phankyanh.shopbongda.pricing.mapper.HolidayPricingRuleMapper;
import vn.phankyanh.shopbongda.pricing.repository.HolidayPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.service.HolidayPricingRuleService;

@Service
@RequiredArgsConstructor
public class HolidayPricingRuleServiceImpl implements HolidayPricingRuleService {

    private static final int DEFAULT_SLOT_DURATION = 90;

    private final HolidayPricingRuleRepository holidayPricingRuleRepository;
    private final FootballFieldRepository footballFieldRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayPricingRuleMapper holidayPricingRuleMapper;

    @Override
    @Transactional
    public HolidayPricingRuleResponse create(HolidayPricingRuleRequest request) {
        validateRequest(request);
        validateNoOverlap(null, request);

        FootballField field = footballFieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Football field not found"));

        Holiday holiday = holidayRepository.findById(request.getHolidayId())
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found"));

        HolidayPricingRule rule = holidayPricingRuleMapper.toEntity(request);
        rule.setField(field);
        rule.setHoliday(holiday);
        rule.setIsActive(true);
        rule.setSlotDuration(normalizeSlotDuration(request.getSlotDuration()));

        return holidayPricingRuleMapper.toResponse(
                holidayPricingRuleRepository.save(rule)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayPricingRuleResponse> getAll(Long fieldId, Long holidayId) {
        return holidayPricingRuleRepository
                .searchActive(fieldId, holidayId)
                .stream()
                .map(holidayPricingRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public HolidayPricingRuleResponse update(Long id, HolidayPricingRuleRequest request) {
        validateRequest(request);
        validateNoOverlap(id, request);

        HolidayPricingRule rule = holidayPricingRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday pricing rule not found"));

        FootballField field = footballFieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Football field not found"));

        Holiday holiday = holidayRepository.findById(request.getHolidayId())
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found"));

        holidayPricingRuleMapper.updateEntity(request, rule);
        rule.setField(field);
        rule.setHoliday(holiday);
        rule.setSlotDuration(normalizeSlotDuration(request.getSlotDuration()));

        return holidayPricingRuleMapper.toResponse(
                holidayPricingRuleRepository.save(rule)
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HolidayPricingRule rule = holidayPricingRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday pricing rule not found"));

        rule.setIsActive(false);
        holidayPricingRuleRepository.save(rule);
    }

    private void validateRequest(HolidayPricingRuleRequest request) {
        if (request.getFieldId() == null) {
            throw new IllegalArgumentException("Field ID is required");
        }

        if (request.getHolidayId() == null) {
            throw new IllegalArgumentException("Holiday ID is required");
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (request.getPricePerSlot() == null
                || request.getPricePerSlot().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per slot must be greater than 0");
        }

        if (request.getSlotDuration() != null && request.getSlotDuration() <= 0) {
            throw new IllegalArgumentException("Slot duration must be greater than 0");
        }
    }

    private void validateNoOverlap(Long currentRuleId, HolidayPricingRuleRequest request) {
        boolean hasOverlap = holidayPricingRuleRepository
                .findActiveOverlaps(
                        request.getFieldId(),
                        request.getHolidayId(),
                        request.getStartTime(),
                        request.getEndTime()
                )
                .stream()
                .anyMatch(rule -> !rule.getId().equals(currentRuleId));

        if (hasOverlap) {
            throw new IllegalArgumentException("Holiday pricing rule overlaps with an existing rule");
        }
    }

    private Integer normalizeSlotDuration(Integer slotDuration) {
        return slotDuration == null ? DEFAULT_SLOT_DURATION : slotDuration;
    }
}
