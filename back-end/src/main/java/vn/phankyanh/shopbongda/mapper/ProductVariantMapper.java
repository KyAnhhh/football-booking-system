package vn.phankyanh.shopbongda.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.dto.response.ProductVariantResponse;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "soldQuantity", ignore = true)
    ProductVariant toCreateProductVariant(ProductVariantRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.code", target = "sizeCode")
    @Mapping(source = "product.id", target = "productId")
    ProductVariantResponse toResponse(ProductVariant variant);

    List<ProductVariantResponse> toResponses(List<ProductVariant> variants);

    @Mapping(target = "price", ignore = true)
    @Mapping(target = "stock", ignore = true)
    ProductVariant updateProductVariant(ProductVariant productVariant, ProductVariantRequest request);
}
