package vn.phankyanh.shopbongda.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.phankyanh.shopbongda.domain.Brand;
import vn.phankyanh.shopbongda.dto.request.BrandRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.BrandResponse;
import vn.phankyanh.shopbongda.service.BrandService;


import java.util.List;

@RestController
@Data
@RequiredArgsConstructor
@RequestMapping("/api/admin/brands")
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrand(){
        ApiResponse<List<BrandResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleGetAllBrand());
        return apiResponse;
    }

    @PostMapping
    public ApiResponse<BrandResponse> createBrand(@RequestBody BrandRequest request){
        ApiResponse<BrandResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleCreateBrand(request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBrand(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.brandService.handleDeleteBrand(id);
        apiResponse.setMessage("Deleted user successfully with id: " + id);
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getUserById(@PathVariable("id") Long id){
        ApiResponse<BrandResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleGetUserById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<Brand> updateBrand(@PathVariable("id") Long id, @RequestBody BrandRequest request){
        ApiResponse<Brand> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.brandService.handleUpdateBrand(id, request));
        return apiResponse;
    }
}
