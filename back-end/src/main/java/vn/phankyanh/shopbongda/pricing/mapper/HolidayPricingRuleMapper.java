package vn.phankyanh.shopbongda.pricing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.phankyanh.shopbongda.pricing.domain.HolidayPricingRule;
import vn.phankyanh.shopbongda.pricing.dto.request.HolidayPricingRuleRequest;
import vn.phankyanh.shopbongda.pricing.dto.response.HolidayPricingRuleResponse;

@Mapper(componentModel = "spring")
public interface HolidayPricingRuleMapper {

    @Mapping(target = "field", ignore = true)
    @Mapping(target = "holiday", ignore = true)
    HolidayPricingRule toEntity(HolidayPricingRuleRequest request);

    @Mapping(target = "fieldId", source = "field.id")
    @Mapping(target = "fieldName", source = "field.name")
    @Mapping(target = "holidayId", source = "holiday.id")
    @Mapping(target = "holidayName", source = "holiday.name")
    @Mapping(target = "holidayDate", source = "holiday.holidayDate")
    HolidayPricingRuleResponse toResponse(HolidayPricingRule entity);

    @Mapping(target = "field", ignore = true)
    @Mapping(target = "holiday", ignore = true)
    void updateEntity(
            HolidayPricingRuleRequest request,
            @MappingTarget HolidayPricingRule entity
    );
}
