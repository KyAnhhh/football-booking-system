package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.CartItemResponse;
import vn.phankyanh.shopbongda.service.CartItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ApiResponse<CartItemResponse> addItem(@RequestBody CartItemRequest request) {
        ApiResponse<CartItemResponse> res = new ApiResponse<>();
        res.setData(cartItemService.addCartItem(request));
        return res;
    }

    @GetMapping("/cart/{cartId}")
    public ApiResponse<List<CartItemResponse>> getItems(@PathVariable Long cartId) {
        ApiResponse<List<CartItemResponse>> res = new ApiResponse<>();
        res.setData(cartItemService.getItemsByCart(cartId));
        return res;
    }

    @PutMapping("/{itemId}")
    public ApiResponse<CartItemResponse> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {
        ApiResponse<CartItemResponse> res = new ApiResponse<>();
        res.setData(cartItemService.updateQuantity(itemId, quantity));
        return res;
    }

    @DeleteMapping("/{itemId}")
    public ApiResponse<String> delete(@PathVariable Long itemId) {
        cartItemService.deleteCartItem(itemId);
        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Deleted item " + itemId);
        return res;
    }

    @DeleteMapping("/cart/{cartId}/clear")
    public ApiResponse<String> clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Cleared all items in cart " + cartId);
        return res;
    }
}
