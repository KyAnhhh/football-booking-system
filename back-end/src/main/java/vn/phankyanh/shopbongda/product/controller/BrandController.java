package vn.phankyanh.shopbongda.product.controller;

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
import vn.phankyanh.shopbongda.product.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.product.dto.response.BrandResponse;
import vn.phankyanh.shopbongda.product.service.BrandService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping("/api/public/brands")
    public ApiResponse<Page<BrandResponse>> getAllBrand(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String search
    ){
        ApiResponse<Page<BrandResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleGetAllBrand(pageNo, pageSize, sortBy, sortDir, search));
        return apiResponse;
    }

    @PostMapping("/api/admin/brands")
    public ApiResponse<BrandResponse> createBrand(@RequestBody BrandRequest request){
        ApiResponse<BrandResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleCreateBrand(request));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/brands/{id}")
    public ApiResponse<String> deleteBrand(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.brandService.handleDeleteBrand(id);
        apiResponse.setMessage("Deleted brand successfully with id: " + id);
        return apiResponse;
    }

    @GetMapping("/api/public/brands/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable("id") Long id){
        ApiResponse<BrandResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleGetBrandById(id));
        return apiResponse;
    }

    @PutMapping("/api/admin/brands/{id}")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable("id") Long id, @RequestBody BrandRequest request){
        ApiResponse<BrandResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleUpdateBrand(id, request));
        return apiResponse;
    }
}
