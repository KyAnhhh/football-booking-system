package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.ProductRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.service.ProductService;

@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/admin/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> createProduct(@ModelAttribute ProductRequest request,
                                                     @RequestPart(value = "image", required = false) MultipartFile image,
                                                     HttpServletRequest servletRequest) {
        // ensure image from multipart is set on request (some clients bind differently)
        if (image != null) {
            request.setImage(image);
        }
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.productService.handleCreateProduct(request));
        return apiResponse;
    }

    // also accept JSON payloads (no file upload) for clients that send imageUrl directly
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> createProductJson(@org.springframework.web.bind.annotation.RequestBody ProductRequest request) {
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> updateProductJson(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody ProductRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
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
