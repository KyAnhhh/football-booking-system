package vn.phankyanh.shopbongda.wishlist.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.wishlist.dto.WishlistItemResponse;
import vn.phankyanh.shopbongda.wishlist.service.WishlistService;

@RestController
@RequestMapping("/api/user/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<WishlistItemResponse>> getMine(
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApiResponse<List<WishlistItemResponse>> response = new ApiResponse<>();
        response.setData(wishlistService.getByUser(userId(jwt)));
        return response;
    }

    @PostMapping("/{productId}")
    public ApiResponse<WishlistItemResponse> add(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApiResponse<WishlistItemResponse> response = new ApiResponse<>();
        response.setData(wishlistService.add(userId(jwt), productId));
        response.setMessage("Added product to wishlist");
        return response;
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<String> remove(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        wishlistService.remove(userId(jwt), productId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Removed product from wishlist");
        return response;
    }

    @GetMapping("/{productId}/exists")
    public ApiResponse<Map<String, Boolean>> contains(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApiResponse<Map<String, Boolean>> response = new ApiResponse<>();
        response.setData(Map.of(
                "exists",
                wishlistService.contains(userId(jwt), productId)
        ));
        return response;
    }

    private Long userId(Jwt jwt) {
        return jwt.getClaim("id");
    }
}
