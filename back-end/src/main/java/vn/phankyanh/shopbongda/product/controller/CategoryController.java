package vn.phankyanh.shopbongda.product.controller;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.product.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.product.service.CategoryService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/public/categories")
    public ApiResponse<Page<CategoryResponse>> getAllCategory(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search
    ){
        ApiResponse<Page<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.getAllCategory(pageNo, pageSize, sortBy, sortDir, search));
        apiResponse.setMessage("Get all categories successfully");
        return apiResponse;
    }

    @PostMapping("/admin/categories")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.createCategory(request));
        apiResponse.setMessage("Created category successfully");
        return apiResponse;
    }

    @GetMapping("/public/categories/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable("id") Long id){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.getCategoryById(id));
        apiResponse.setMessage("Get category by id successfully");
        return apiResponse;
    }

    @PutMapping("/admin/categories/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest request){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.updateCategory(id, request));
        apiResponse.setMessage("Updated category successfully");
        return apiResponse;
    }

    @DeleteMapping("/admin/categories/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.categoryService.deleteCategory(id);
        apiResponse.setMessage("Deleted category successfully with id: " + id);
        return apiResponse;
    }
}
