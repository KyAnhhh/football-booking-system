package vn.phankyanh.shopbongda.product.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.product.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.product.dto.response.CategoryResponse;

public interface CategoryService {
    Page<CategoryResponse> getAllCategory(int pageNo, int pageSize, String sortBy, String sortDir, String search);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);
}
