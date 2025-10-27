package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.Category;
import vn.phankyanh.shopbongda.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.dto.response.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toResponse(Category category);
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
