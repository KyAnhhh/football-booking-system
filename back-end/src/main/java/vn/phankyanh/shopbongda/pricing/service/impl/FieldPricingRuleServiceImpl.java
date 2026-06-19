package vn.phankyanh.shopbongda.pricing.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.dto.request.FieldPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.FieldPricingRuleResponse;
import vn.phankyanh.shopbongda.pricing.mapper.FieldPricingRuleMapper;
import vn.phankyanh.shopbongda.pricing.repository.FieldPricingRuleRepository;
import vn.phankyanh.shopbongda.pricing.service.FieldPricingRuleService;

@Service
@RequiredArgsConstructor
public class FieldPricingRuleServiceImpl
    implements FieldPricingRuleService {

    private final FieldPricingRuleRepository fieldPricingRuleRepository;

    private final FootballFieldRepository footballFieldRepository;

    private final FieldPricingRuleMapper fieldPricingRuleMapper;

    @Override
    public FieldPricingRuleResponse create(
            FieldPricingRuleRequest request) {

        validateRequest(request);

        FootballField field =
                footballFieldRepository.findById(
                                request.getFieldId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Football field not found"));

        FieldPricingRule rule =
                fieldPricingRuleMapper.toEntity(request);

        rule.setField(field);
        rule.setIsActive(true);
        if (rule.getSlotDuration() == null) {
            rule.setSlotDuration(90);
        }

        FieldPricingRule savedRule =
                fieldPricingRuleRepository.save(rule);

        return fieldPricingRuleMapper.toResponse(savedRule);
    }

    @Override
    public List<FieldPricingRuleResponse> getAll() {

        return fieldPricingRuleRepository
                .findAllActive()
                .stream()
                .map(fieldPricingRuleMapper::toResponse)
                .toList();
    }

    @Override
    public List<FieldPricingRuleResponse> getByField(
            Long fieldId) {

        return fieldPricingRuleRepository
                .findActiveByFieldId(fieldId)
                .stream()
                .map(fieldPricingRuleMapper::toResponse)
                .toList();
    }

    @Override
    public FieldPricingRuleResponse update(
            Long id,
            FieldPricingRuleRequest request) {

        validateRequest(request);

        FieldPricingRule rule =
                fieldPricingRuleRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pricing rule not found"));

        fieldPricingRuleMapper.updateEntity(
                request,
                rule);

        FootballField field =
                footballFieldRepository.findById(
                                request.getFieldId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Football field not found"));

        rule.setField(field);
        if (rule.getSlotDuration() == null) {
            rule.setSlotDuration(90);
        }

        FieldPricingRule updatedRule =
                fieldPricingRuleRepository.save(rule);

        return fieldPricingRuleMapper.toResponse(updatedRule);
    }

    @Override
    public void delete(Long id) {

        FieldPricingRule rule =
                fieldPricingRuleRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pricing rule not found"));

        rule.setIsActive(false);

        fieldPricingRuleRepository.save(rule);
    }

    private void validateRequest(
            FieldPricingRuleRequest request) {

        if (request.getFieldId() == null) {
            throw new IllegalArgumentException(
                    "Field ID is required");
        }

        if (request.getDayOfWeek() == null) {
            throw new IllegalArgumentException(
                    "Day of week is required");
        }

        if (request.getStartTime() == null
                || request.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(
                request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        if (request.getPricePerSlot() == null
                || request.getPricePerSlot()
                        .compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Price per slot must be greater than 0");
        }

        if (request.getSlotDuration() != null && request.getSlotDuration() <= 0) {
            throw new IllegalArgumentException(
                    "Slot duration must be greater than 0");
        }
    }

}
