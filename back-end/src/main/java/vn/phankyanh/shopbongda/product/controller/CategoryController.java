package vn.phankyanh.shopbongda.product.controller;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.product.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.product.service.CategoryService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/api/public/categories")
    public ApiResponse<Page<CategoryResponse>> getAllCategory(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search
    ){
        ApiResponse<Page<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleGetAllCategory(pageNo, pageSize, sortBy, sortDir, search));
        return apiResponse;
    }

    @PostMapping("/api/admin/categories")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleCreateCategory(request));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/categories/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.categoryService.handleDeleteCategory(id);
        apiResponse.setMessage("Deleted category successfully with id: " + id);
        return apiResponse;
    }

    @GetMapping("/api/public/categories/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable("id") Long id){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleGetCategoryById(id));
        return apiResponse;
    }

    @PutMapping("/api/admin/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest request){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleUpdateCategory(id, request));
        return apiResponse;
    }
}
