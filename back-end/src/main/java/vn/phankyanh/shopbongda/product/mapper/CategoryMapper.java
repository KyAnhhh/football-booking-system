package vn.phankyanh.shopbongda.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.product.domain.Category;
import vn.phankyanh.shopbongda.product.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.product.dto.response.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toResponse(Category category);
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
