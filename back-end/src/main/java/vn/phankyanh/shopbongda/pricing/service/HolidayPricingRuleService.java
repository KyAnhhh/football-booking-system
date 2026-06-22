package vn.phankyanh.shopbongda.pricing.service;

import java.util.List;

import vn.phankyanh.shopbongda.pricing.dto.request.HolidayPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.HolidayPricingRuleResponse;

public interface HolidayPricingRuleService {

    HolidayPricingRuleResponse create(HolidayPricingRuleRequest request);

    List<HolidayPricingRuleResponse> getAll(Long fieldId, Long holidayId);

    HolidayPricingRuleResponse update(Long id, HolidayPricingRuleRequest request);

    void delete(Long id);
}
