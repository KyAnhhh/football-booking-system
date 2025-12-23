package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ProductVariantResponse;
import vn.phankyanh.shopbongda.service.ProductVariantService;

@RestController
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/api/admin/product-variants")
    public ApiResponse<ProductVariantResponse> createProduct(@RequestBody ProductVariantRequest request) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.handleCreateProductVariant(request));
        return apiResponse;
    }

    @GetMapping("/api/public/product-variants")
    public ApiResponse<List<ProductVariantResponse>> getAllProduct() {
        ApiResponse<List<ProductVariantResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.handleGetAllProductVariant());
        return apiResponse;
    }

    @GetMapping("/api/public/product-variants/{id}")
    public ApiResponse<ProductVariantResponse> getProductById(@PathVariable("id") Long id) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.handleGetProductVariantById(id));
        return apiResponse;
    }

    @PutMapping("/api/admin/product-variants/{id}")
    public ApiResponse<ProductVariantResponse> updateProduct(@PathVariable("id") Long id,
            @RequestBody ProductVariantRequest request) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.handleUpdateProductVariant(id, request));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/product-variants/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productVariantService.handDeleteProductVariant(id);
        apiResponse.setMessage("Deleted product variant with id: " + id);
        return apiResponse;
    }
}
