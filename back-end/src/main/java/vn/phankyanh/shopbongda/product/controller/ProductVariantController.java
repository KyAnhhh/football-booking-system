package vn.phankyanh.shopbongda.product.controller;

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
import vn.phankyanh.shopbongda.product.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductVariantResponse;
import vn.phankyanh.shopbongda.product.service.ProductVariantService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/admin/product-variants")
    public ApiResponse<ProductVariantResponse> createProduct(@RequestBody ProductVariantRequest request) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.createProductVariant(request));
        apiResponse.setMessage("Created product variant successfully");
        return apiResponse;
    }

    @GetMapping("/public/product-variants")
    public ApiResponse<List<ProductVariantResponse>> getAllProduct() {
        ApiResponse<List<ProductVariantResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.getAllProductVariant());
        apiResponse.setMessage("Get all product variants successfully");
        return apiResponse;
    }

    @GetMapping("/public/product-variants/{id}")
    public ApiResponse<ProductVariantResponse> getProductVariantById(@PathVariable("id") Long id) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.getProductVariantById(id));
        apiResponse.setMessage("Get product variant by id successfully");
        return apiResponse;
    }

    @GetMapping("/public/products/{productId}/variants")
    public ApiResponse<List<ProductVariantResponse>> getByProductId(
            @PathVariable Long productId
    ) {
        ApiResponse<List<ProductVariantResponse>> response = new ApiResponse<>();
        response.setData(productVariantService.getByProductId(productId));
        response.setMessage("Get product variants successfully");
        return response;
    }

    @PutMapping("/admin/product-variants/{id}")
    public ApiResponse<ProductVariantResponse> updateProductVariant(@PathVariable("id") Long id,
            @RequestBody ProductVariantRequest request) {
        ApiResponse<ProductVariantResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productVariantService.updateProductVariant(id, request));
        apiResponse.setMessage("Updated product variant successfully");
        return apiResponse;
    }

    @DeleteMapping("/admin/product-variants/{id}")
    public ApiResponse<String> deleteProductVariant(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productVariantService.deleteProductVariant(id);
        apiResponse.setMessage("Deleted product variant with id: " + id);
        return apiResponse;
    }
}
