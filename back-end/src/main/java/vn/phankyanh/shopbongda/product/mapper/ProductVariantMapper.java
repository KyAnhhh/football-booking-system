package vn.phankyanh.shopbongda.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductVariantResponse;

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

    @Mapping(target = "inventoryItemId", ignore = true)
    ProductVariant updateProductVariant(
            ProductVariantRequest request,
            @MappingTarget ProductVariant productVariant
    );
}
