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
import vn.phankyanh.shopbongda.product.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.product.dto.response.SizeResponse;
import vn.phankyanh.shopbongda.product.service.SizeService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SizeController {
    private final SizeService sizeService;

    @GetMapping("/public/sizes")
    public ApiResponse<List<SizeResponse>> getAllSizes(){
        ApiResponse<List<SizeResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.getAllSize());
        return apiResponse;
    }

    @PostMapping("/admin/sizes")
    public ApiResponse<SizeResponse> createSize(@RequestBody SizeRequest request){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.createSize(request));
        apiResponse.setMessage("Created size successfully");
        return apiResponse;
    }

    @PutMapping("/admin/sizes/{id}")
    public ApiResponse<SizeResponse> updateSize(@PathVariable("id") Long id, @RequestBody SizeRequest request){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.updateSize(id, request));
        apiResponse.setMessage("Updated size successfully");
        return apiResponse;
    }

    @GetMapping("/public/sizes/{id}")
    public ApiResponse<SizeResponse> getSizeById(@PathVariable("id") Long id){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();    
        apiResponse.setData(this.sizeService.getSizeById(id));
        apiResponse.setMessage("Get size by id successfully");
        return apiResponse;
    }

    @DeleteMapping("/admin/sizes/{id}")
    public ApiResponse<String> deleteSize(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.sizeService.deleteSize(id);
        apiResponse.setMessage("Deleted size successfully with id: " + id);
        return apiResponse;
    }

}
