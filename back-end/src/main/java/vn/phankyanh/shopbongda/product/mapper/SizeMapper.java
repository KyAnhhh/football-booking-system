package vn.phankyanh.shopbongda.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.product.dto.response.SizeResponse;

@Mapper(componentModel = "spring")
public interface SizeMapper {

    Size toSize(SizeRequest request);

    SizeResponse toResponse(Size size);

    void updateSize(@MappingTarget Size size, SizeRequest request);
}
