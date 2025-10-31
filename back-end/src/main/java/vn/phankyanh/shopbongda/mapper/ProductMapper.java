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
    Product toProduct(ProductRequest request);

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "sold_quantity", target = "soldQuantity")
    ProductResponse toProductResponse(Product product);

    void updateProduct(@MappingTarget Product product, ProductRequest request);
}
