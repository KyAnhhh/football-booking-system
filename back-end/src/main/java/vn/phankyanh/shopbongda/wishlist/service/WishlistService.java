package vn.phankyanh.shopbongda.wishlist.service;

import java.util.List;

import vn.phankyanh.shopbongda.wishlist.dto.WishlistItemResponse;

public interface WishlistService {
    List<WishlistItemResponse> getByUser(Long userId);
    WishlistItemResponse add(Long userId, Long productId);
    void remove(Long userId, Long productId);
    boolean contains(Long userId, Long productId);
}
