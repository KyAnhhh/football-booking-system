package vn.phankyanh.shopbongda.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.CartResponse;
import vn.phankyanh.shopbongda.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ApiResponse<CartResponse> addItemToCart(@RequestBody CartItemRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.addItemToCart(userId, request));
        return apiResponse;
    }


}
