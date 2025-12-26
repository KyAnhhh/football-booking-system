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
import vn.phankyanh.shopbongda.dto.request.SizeRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.SizeResponse;
import vn.phankyanh.shopbongda.service.SizeService;

@RestController
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;

    @GetMapping("/api/public/sizes")
    public ApiResponse<List<SizeResponse>> getAllSizes(){
        ApiResponse<List<SizeResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.handleGetAllSize());
        return apiResponse;
    }

    @PostMapping("/api/admin/sizes")
    public ApiResponse<SizeResponse> createSize(@RequestBody SizeRequest request){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.handleCreateSize(request));
        return apiResponse;
    }

    @PutMapping("/api/admin/sizes/{id}")
    public ApiResponse<SizeResponse> updateSize(@PathVariable("id") Long id, @RequestBody SizeRequest request){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.sizeService.handleUpdateSize(id, request));
        return apiResponse;
    }

    @GetMapping("/api/public/sizes/{id}")
    public ApiResponse<SizeResponse> getSizeById(@PathVariable("id") Long id){
        ApiResponse<SizeResponse> apiResponse = new ApiResponse<>();    
        apiResponse.setData(this.sizeService.handleGetSizeById(id));
        return apiResponse;
    }

    @DeleteMapping("/api/admin/sizes/{id}")
    public ApiResponse<String> deleteSize(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.sizeService.handleDeleteSize(id);
        apiResponse.setMessage("Deleted size successfully with id: " + id);
        return apiResponse;
    }

}
