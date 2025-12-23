package vn.phankyanh.shopbongda.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.domain.Size;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.response.CartResponse;
import vn.phankyanh.shopbongda.mapper.CartMapper;
import vn.phankyanh.shopbongda.repository.CartItemRepository;
import vn.phankyanh.shopbongda.repository.CartRepository;
import vn.phankyanh.shopbongda.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.repository.SizeRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SizeRepository sizeRepository;

    //add item to cart
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        Cart cart =  cartRepository.findByUserId(userId)
            .orElseGet(() -> createNewCart(user));

        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
            .orElseThrow(() -> new RuntimeException("Product variant not found with id " + request.getProductVariantId()));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));
        
        if (!productVariant.getSize().equals(size)) {
            throw new RuntimeException("Size does not belong to this variant");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductVariantAndSize(cart, productVariant, size)
            .orElseGet(() -> createNewCartItem(cart, productVariant, size));

        // check kĩ stock khi thêm sản phẩm vào giỏ hàng
        int newQuantity = cartItem.getTotalQuantity() + request.getQuantity();

        if(productVariant.getStock() < newQuantity){
            throw new RuntimeException("No enough stock");
        }

        cartItem.setTotalQuantity(newQuantity);

        cartItemRepository.save(cartItem);

        recalculateCartTotal(cart);

        return cartMapper.toCartResponse(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .build();
        System.out.println(cart.getItems());
        return cartRepository.save(cart);
    }

    private CartItem createNewCartItem(Cart cart, ProductVariant productVariant, Size size) {
        CartItem item = CartItem.builder()
                .cart(cart)
                .productVariant(productVariant)
                .productId(productVariant.getProduct().getId())
                .productName(productVariant.getProduct().getName())
                .size(size)
                .totalQuantity(0)
                .priceAtAddedTime(productVariant.getPrice())
                .build();

        cart.getItems().add(item);
        return item;
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPriceAtAddedTime()
                        .multiply(BigDecimal.valueOf(i.getTotalQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }
}
