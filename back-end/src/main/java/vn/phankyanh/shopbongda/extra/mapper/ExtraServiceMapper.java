package vn.phankyanh.shopbongda.extra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;
import vn.phankyanh.shopbongda.extra.dto.request.ExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;

@Mapper(componentModel = "spring")
public interface ExtraServiceMapper {

    ExtraService toEntity(
            ExtraServiceRequest request
    );

    ExtraServiceResponse toResponse(
            ExtraService entity
    );

    void updateEntity(
            ExtraServiceRequest request,
            @MappingTarget ExtraService entity
    );
}