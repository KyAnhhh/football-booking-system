package vn.phankyanh.shopbongda.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.service.ShippingAddressService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/shippingAddress")
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @PostMapping
    public ApiResponse<ShippingAddressResponse> createShippingAddress(@RequestBody ShippingAddressRequest request, @RequestHeader("Authorization") String authHeader) throws ParseException {
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleCreateShippingAddress(request, authHeader));
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
