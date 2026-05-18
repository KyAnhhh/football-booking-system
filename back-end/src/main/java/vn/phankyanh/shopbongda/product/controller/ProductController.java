package vn.phankyanh.shopbongda.product.controller;

import java.math.BigDecimal;
import java.util.List;

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
import vn.phankyanh.shopbongda.product.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.product.service.ProductService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/admin/products")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleCreateProduct(request));
        return apiResponse;
    }

    @GetMapping("/api/public/products")
    public ApiResponse<Page<ProductResponse>> getAllProduct(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        ApiResponse<Page<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetAllProduct(pageNo, pageSize, sortBy, sortDir, search, category, brand, minPrice, maxPrice));
        return apiResponse;
    }

    @GetMapping("/api/public/products/top4-popular")
    public ApiResponse<List<ProductResponse>> getTop4PopularProducts() {
        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetTop4PopularProducts());
        return apiResponse;
    }

    @GetMapping("/api/public/products/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetProductById(id));
        return apiResponse;
    }

    @PutMapping("/api/admin/products/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("id") Long id,
            @RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleUpdateProduct(id, request));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productService.handleDeleteProduct(id);
        apiResponse.setMessage("Deleted product with id: " + id);
        return apiResponse;
    }

}
