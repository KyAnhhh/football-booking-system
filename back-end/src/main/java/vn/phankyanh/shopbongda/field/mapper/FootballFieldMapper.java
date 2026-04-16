package vn.phankyanh.shopbongda.field.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.dto.request.FootballFieldRequest;
import vn.phankyanh.shopbongda.field.dto.response.FootballFieldResponse;

@Mapper(componentModel = "spring")
public interface FootballFieldMapper {
    
    @Mapping(target = "type", expression = "java(footballField.getType().name())")
    @Mapping(target = "status", expression = "java(footballField.getStatus().name())")
    FootballFieldResponse toResponse(FootballField footballField);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    FootballField toFootballField(FootballFieldRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateField(@MappingTarget FootballField field, FootballFieldRequest request);
}
