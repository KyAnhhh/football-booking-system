package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.dto.response.ProductResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Product toProduct(ProductRequest request);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "imageUrl", target = "imageUrl")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
