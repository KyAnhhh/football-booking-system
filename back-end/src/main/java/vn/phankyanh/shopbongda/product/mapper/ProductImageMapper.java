package vn.phankyanh.shopbongda.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import vn.phankyanh.shopbongda.product.domain.ProductImage;
import vn.phankyanh.shopbongda.product.dto.response.ProductImageResponse;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    ProductImageResponse toResponse(ProductImage image);

    List<ProductImageResponse> toResponseList(List<ProductImage> images);

    // helper mappings to extract image URL strings for ProductResponse.imageUrls
    default String toUrl(ProductImage image) {
        return image == null ? null : image.getImageUrl();
    }

    default List<String> toUrlList(List<ProductImage> images) {
        if (images == null) return null;
        return images.stream()
                .map(this::toUrl)
                .toList();
    }
}
