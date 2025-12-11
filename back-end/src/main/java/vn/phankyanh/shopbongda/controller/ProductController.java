package vn.phankyanh.shopbongda.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.service.ProductService;

@RestController
@RequiredArgsConstructor 
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/api/admin/products",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> createProduct(@ModelAttribute ProductRequest request,
                                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                                     HttpServletRequest servletRequest) {
        if (image != null) {
            request.setImage(image);
        }
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
        @RequestParam(required = false) String search
    ){
        ApiResponse<Page<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetAllProduct(pageNo, pageSize, sortBy, sortDir, search));
        return apiResponse;
    }

    @GetMapping("/api/public/products/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id){
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleGetProductById(id));
        return apiResponse;
    }

    @PutMapping(value = "/api/admin/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("id") Long id,
                                                     @ModelAttribute ProductRequest request,
                                                     @RequestPart(value = "image", required = false) MultipartFile image) {
        if (image != null) {
            request.setImage(image);
        }
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleUpdateProduct(id, request));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.productService.handleDeleteProduct(id);
        apiResponse.setMessage("Deleted product with id: " + id);
        return apiResponse;
    }
    
}
