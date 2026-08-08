package vn.phankyanh.shopbongda.order.service;

import vn.phankyanh.shopbongda.order.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.order.dto.request.UpdateCartItemQuantityRequest;
import vn.phankyanh.shopbongda.order.dto.response.CartResponse;

public interface CartService {

    CartResponse getCartByUser(Long userId);

    CartResponse addItemToCart(Long userId, CartItemRequest request);

    CartResponse updateItemQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request);

    void removeItemFromCart(Long userId, Long cartItemId);
}
