package vn.phankyanh.shopbongda.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.product.domain.Brand;
import vn.phankyanh.shopbongda.product.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.product.dto.response.BrandResponse;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandRequest request);
    BrandResponse toResponse(Brand brand);
    void updateBrand(@MappingTarget Brand brand, BrandRequest request);
}
