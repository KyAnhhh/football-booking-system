package vn.phankyanh.shopbongda.controller;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Category;
import vn.phankyanh.shopbongda.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.service.CategoryService;

@RestController
@Data
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
        apiResponse.setMessage("Deleted user successfully with id: " + id);
        return apiResponse;
    }

    @GetMapping("/api/admin/categories/{id}")
    public ApiResponse<CategoryResponse> getUserById(@PathVariable("id") Long id){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleGetUserById(id));
        return apiResponse;
    }

    @PutMapping("/api/admin/categories/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest request){
        ApiResponse<Category> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleUpdateCategory(id, request));
        return apiResponse;
    }
}
