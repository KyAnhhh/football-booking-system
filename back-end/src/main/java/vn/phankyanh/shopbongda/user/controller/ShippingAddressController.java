package vn.phankyanh.shopbongda.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.user.service.ShippingAddressService;


@RestController
@RequiredArgsConstructor
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @PostMapping("/api/user/shipping-addresses")
    public ApiResponse<ShippingAddressResponse> createShippingAddress(@RequestBody ShippingAddressRequest request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleCreateShippingAddress(request, userId));
        return apiResponse;
    }

    @GetMapping("/api/user/shipping-addresses/me")
    public ApiResponse<ShippingAddressResponse> getMyAddress(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        
        ShippingAddressResponse address = shippingAddressService.handleGetMyAddress(userId);

        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(address);
        return apiResponse;
    }

    @GetMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<ShippingAddressResponse> getShippingAddressById(@PathVariable("id") Long id){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleGetShippingAddressById(id));
        return apiResponse;
    }

    @PutMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<ShippingAddressResponse> updateShippingAddress(@PathVariable("id") Long id, @RequestBody ShippingAddressRequest request){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.handleUpdate(id, request));
        return apiResponse;
    }

    @DeleteMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<String> deleteShippingAddress(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Deleted successfully with id "+ id);
        return apiResponse;
    }
}
