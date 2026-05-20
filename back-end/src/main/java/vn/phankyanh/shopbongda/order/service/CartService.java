package vn.phankyanh.shopbongda.order.service;

import vn.phankyanh.shopbongda.order.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.order.dto.response.CartResponse;

public interface CartService {

    CartResponse getCartByUser(Long userId);

    CartResponse addItemToCart(Long userId, CartItemRequest request);

    void removeItemFromCart(Long userId, Long cartItemId);
}