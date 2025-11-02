package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.domain.ShippingAddress;
import vn.phankyanh.shopbongda.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.service.ShippingAddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/shippingAddress")
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @PostMapping
    public ApiResponse<ShippingAddressResponse> createShippingAddress(@RequestBody ShippingAddressRequest request){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleCreateShippingAddress(request));
        return apiResponse;
    }
    @GetMapping
    public ApiResponse<List<ShippingAddressResponse>> getAllShippingAddress(){
        ApiResponse<List<ShippingAddressResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleGetAllShippingAddress());
        return  apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<ShippingAddressResponse> getShippingAddressById(@PathVariable("id") Long id){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleGetShippingAddressById(id));
        return  apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<ShippingAddressResponse> updateShippingAddress(@PathVariable("id") Long id, @RequestBody ShippingAddressRequest request){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleUpdate(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteShippingAddress(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Deleted successfully with id "+ id);
        return apiResponse;
    }
}
