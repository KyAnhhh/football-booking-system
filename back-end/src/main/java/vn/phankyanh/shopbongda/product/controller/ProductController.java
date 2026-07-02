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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.product.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.product.service.ProductService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.catalog.dto.PublicationCheckResponse;
import vn.phankyanh.shopbongda.catalog.service.CatalogPublicationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;
    private final CatalogPublicationService publicationService;

    @PostMapping("/admin/products")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.createProduct(request));
        apiResponse.setMessage("Created product successfully");
        return apiResponse;
    }

    @GetMapping("/public/products")
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
        apiResponse.setData(this.productService.getAllProducts(pageNo, pageSize, sortBy, sortDir, search, category, brand, minPrice, maxPrice));
        apiResponse.setMessage("Get all products successfully");
        return apiResponse;
    }

    @GetMapping("/admin/products")
    public ApiResponse<Page<ProductResponse>> getAdminProducts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        ApiResponse<Page<ProductResponse>> response = new ApiResponse<>();
        response.setData(productService.getAdminProducts(
                pageNo, pageSize, sortBy, sortDir, search
        ));
        return response;
    }

    @GetMapping("/admin/products/{id}/publication-check")
    public ApiResponse<PublicationCheckResponse> checkPublication(
            @PathVariable Long id
    ) {
        ApiResponse<PublicationCheckResponse> response = new ApiResponse<>();
        response.setData(publicationService.checkProduct(id));
        return response;
    }

    @PostMapping("/admin/products/{id}/publish")
    public ApiResponse<ProductResponse> publish(@PathVariable Long id) {
        ApiResponse<ProductResponse> response = new ApiResponse<>();
        response.setData(publicationService.publishProduct(id));
        return response;
    }

    @PostMapping("/admin/products/{id}/unpublish")
    public ApiResponse<ProductResponse> unpublish(@PathVariable Long id) {
        ApiResponse<ProductResponse> response = new ApiResponse<>();
        response.setData(publicationService.unpublishProduct(id));
        return response;
    }

    @GetMapping("/public/products/top4-popular")
    public ApiResponse<List<ProductResponse>> getTop4PopularProducts() {
        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.getTop4PopularProducts());
        apiResponse.setMessage("Get top 4 popular products successfully");
        return apiResponse;
    }

    @GetMapping("/public/products/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.getProductById(id));
        apiResponse.setMessage("Get product by id successfully");
        return apiResponse;
    }

    @PutMapping("/admin/products/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("id") Long id,
            @RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.updateProduct(id, request));
        apiResponse.setMessage("Updated product successfully");
        return apiResponse;
    }

    @DeleteMapping("/admin/products/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") Long id) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productService.deleteProduct(id);
        apiResponse.setMessage("Deleted product with id: " + id);
        return apiResponse;
    }

}
