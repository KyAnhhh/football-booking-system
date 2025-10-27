package vn.phankyanh.shopbongda.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.domain.Category;
import vn.phankyanh.shopbongda.dto.request.CategoryRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.CategoryResponse;
import vn.phankyanh.shopbongda.service.CategoryService;

import java.util.List;

@RestController
@Data
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategory(){
        ApiResponse<List<Category>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleGetAllCategory());
        return apiResponse;
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleCreateCategory(request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.categoryService.handleDeleteCategory(id);
        apiResponse.setMessage("Deleted user successfully with id: " + id);
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getUserById(@PathVariable("id") Long id){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleGetUserById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryRequest request){
        ApiResponse<Category> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.categoryService.handleUpdateCategory(id, request));
        return apiResponse;
    }
}
