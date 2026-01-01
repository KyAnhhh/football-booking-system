package vn.phankyanh.shopbongda.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItemToCart(@RequestBody CartItemRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.addItemToCart(userId, request));
        return apiResponse;
    }

    @GetMapping("/items/me")
    public ApiResponse<CartResponse> getCartByUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("id");
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.getCartByUser(userId));
        return apiResponse;
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<Void> removeItemFromCart(@AuthenticationPrincipal Jwt jwt, @PathVariable("cartItemId") Long cartItemId) {
        Long userId = jwt.getClaim("id");
        this.cartService.removeItemFromCart(userId, cartItemId);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Item removed from cart successfully");
        return apiResponse;
    }
}