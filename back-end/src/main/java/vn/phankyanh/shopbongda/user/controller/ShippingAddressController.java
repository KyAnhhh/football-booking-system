package vn.phankyanh.shopbongda.user.controller;

import java.util.List;

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
import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @PostMapping("/api/user/shipping-addresses")
    public ApiResponse<ShippingAddressResponse> createShippingAddress(
            @Valid @RequestBody ShippingAddressRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("id");
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.createShippingAddress(request, userId));
        apiResponse.setMessage("Created shipping address successfully");
        return apiResponse;
    }

    @GetMapping("/api/user/shipping-addresses/me")
    public ApiResponse<List<ShippingAddressResponse>> getMyAddress(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("id");

        ApiResponse<List<ShippingAddressResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(shippingAddressService.getMyAddresses(userId));
        apiResponse.setMessage("Get my shipping addresses successfully");
        return apiResponse;
    }

    @GetMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<ShippingAddressResponse> getShippingAddressById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Jwt jwt
    ){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.getShippingAddressById(
                id,
                jwt.getClaim("id")
        ));
        apiResponse.setMessage("Get shipping address by id successfully");
        return apiResponse;
    }

    @PutMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<ShippingAddressResponse> updateShippingAddress(
            @PathVariable("id") Long id,
            @Valid @RequestBody ShippingAddressRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        ApiResponse<ShippingAddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.shippingAddressService.update(
                id,
                request,
                jwt.getClaim("id")
        ));
        apiResponse.setMessage("Updated successfully with id "+ id);
        return apiResponse;
    }

    @DeleteMapping("/api/user/shipping-addresses/{id}")
    public ApiResponse<String> deleteShippingAddress(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Jwt jwt
    ){
        shippingAddressService.delete(id, jwt.getClaim("id"));
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Deleted successfully with id "+ id);
        return apiResponse;
    }
}
