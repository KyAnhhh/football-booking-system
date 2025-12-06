package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.service.ProductService;

@RestController
@RequiredArgsConstructor // inject dependency
@RequestMapping("/api/admin/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleCreateProduct(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProduct(){
        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetAllProduct());
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id){
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetProductById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequest request) {
        ApiResponse<Product> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleUpdateProduct(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productService.handleDeleteProduct(id);
        apiResponse.setMessage("Deleted product with id: " + id);
        return apiResponse;
    }
    
}
