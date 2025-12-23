package vn.phankyanh.shopbongda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.CartItemResponse;
import vn.phankyanh.shopbongda.mapper.CartItemMapper;
import vn.phankyanh.shopbongda.repository.CartItemRepository;
import vn.phankyanh.shopbongda.repository.CartRepository;
import vn.phankyanh.shopbongda.repository.ProductVariantRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemMapper cartItemMapper;

    // Thêm hoặc tăng số lượng item trong cart
    public CartItemResponse addCartItem(CartItemRequest request) {

        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductVariant productVariant = productVariantRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra item có tồn tại không
        CartItem existingItem =
                cartItemRepository.findByCartIdAndProductId(request.getCartId(), request.getProductId());

        CartItem saved;

        if (existingItem != null) {
            // Nếu đã tồn tại → tăng số lượng
            existingItem.setTotalQuantity(
                    existingItem.getTotalQuantity() + request.getQuantity()
            );
            saved = cartItemRepository.save(existingItem);
        } else {
            // Nếu chưa tồn tại → tạo mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .totalQuantity(request.getQuantity())
                    .priceAtAddedTime(productVariant.getPrice())
                    .build();

            saved = cartItemRepository.save(newItem);
        }

        return cartItemMapper.toCartItemResponse(saved);
    }

    // Lấy tất cả item theo cartId
    public List<CartItemResponse> getItemsByCart(Long cartId) {
        return cartItemRepository.findByCartId(cartId)
                .stream()
                .map(cartItemMapper::toCartItemResponse)
                .toList();
    }

    // Update số lượng item
    public CartItemResponse updateQuantity(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        item.setTotalQuantity(quantity);

        CartItem updated = cartItemRepository.save(item);
        return cartItemMapper.toCartItemResponse(updated);
    }

    // Xóa item khỏi giỏ
    public void deleteCartItem(Long itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new RuntimeException("CartItem not found");
        }
        cartItemRepository.deleteById(itemId);
    }

    // Xóa toàn bộ giỏ (optional)
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
}
