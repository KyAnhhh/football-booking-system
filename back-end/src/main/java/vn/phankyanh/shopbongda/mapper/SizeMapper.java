package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.phankyanh.shopbongda.domain.Size;
import vn.phankyanh.shopbongda.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.dto.response.SizeResponse;

@Mapper(componentModel = "spring")
public interface SizeMapper {

    Size toSize(SizeRequest request);

    SizeResponse toResponse(Size size);

    void updateSize(@MappingTarget Size size, SizeRequest request);
}
