package vn.phankyanh.shopbongda.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductImageMapper.class})
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "wishLists", ignore = true)
    @Mapping(target = "publicationStatus", ignore = true)
    Product toProduct(ProductRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "variants", target = "variants")
    @Mapping(source = "images", target = "imageUrls")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "publicationStatus", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
