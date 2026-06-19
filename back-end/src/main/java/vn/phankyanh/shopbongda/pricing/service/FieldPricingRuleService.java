package vn.phankyanh.shopbongda.pricing.service;

import vn.phankyanh.shopbongda.pricing.dto.request.FieldPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.FieldPricingRuleResponse;
import java.util.List;

public interface FieldPricingRuleService {
    FieldPricingRuleResponse create(
            FieldPricingRuleRequest request);

    List<FieldPricingRuleResponse> getAll();

    List<FieldPricingRuleResponse> getByField(
            Long fieldId);

    FieldPricingRuleResponse update(
            Long id,
            FieldPricingRuleRequest request);

    void delete(Long id);
}
