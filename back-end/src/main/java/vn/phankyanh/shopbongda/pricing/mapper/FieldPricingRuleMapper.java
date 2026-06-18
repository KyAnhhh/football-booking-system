package vn.phankyanh.shopbongda.pricing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.phankyanh.shopbongda.pricing.domain.FieldPricingRule;
import vn.phankyanh.shopbongda.pricing.dto.request.FieldPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.FieldPricingRuleResponse;

@Mapper(componentModel = "spring")
public interface FieldPricingRuleMapper {

    FieldPricingRule toEntity(FieldPricingRuleRequest request);

    @Mapping(target = "fieldId", source = "field.id")
    @Mapping(target = "fieldName", source = "field.name")
    FieldPricingRuleResponse toResponse(FieldPricingRule entity);

    @Mapping(target = "field", ignore = true)
    void updateEntity(
            FieldPricingRuleRequest request,
            @MappingTarget FieldPricingRule entity);
}
