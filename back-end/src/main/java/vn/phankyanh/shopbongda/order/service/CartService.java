package vn.phankyanh.shopbongda.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Cart;
import vn.phankyanh.shopbongda.order.domain.CartItem;
import vn.phankyanh.shopbongda.order.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.order.dto.response.CartResponse;
import vn.phankyanh.shopbongda.order.mapper.CartMapper;
import vn.phankyanh.shopbongda.order.repository.CartItemRepository;
import vn.phankyanh.shopbongda.order.repository.CartRepository;
import vn.phankyanh.shopbongda.product.domain.ProductImage;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

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

    // get cart by userId
    public CartResponse getCartByUser(Long userId) {
        return cartRepository.findByUserId(userId)
            .map(cartMapper::toCartResponse)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    //add item to cart
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(user));

        ProductVariant productVariant = productVariantRepository
                .findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

       CartItem cartItem = cartItemRepository
        .findByCartAndProductVariantAndSize(cart, productVariant, size)
        .orElse(null);

        if (cartItem == null) {
        if (productVariant.getStock() < request.getQuantity()) {
                throw new RuntimeException("Not enough stock");
        }

        cartItem = createNewCartItem(cart, productVariant, size);
        cartItem.setTotalQuantity(request.getQuantity()); 
        cartItemRepository.save(cartItem);

        } else {
        int newQuantity = cartItem.getTotalQuantity() + request.getQuantity();

        if (productVariant.getStock() < newQuantity) {
                throw new RuntimeException("Not enough stock");
        }

        cartItem.setTotalQuantity(newQuantity);
        cartItemRepository.save(cartItem);
        }


        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    // create cart
    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .totalAmount(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    //  create cart_items
    private CartItem createNewCartItem(Cart cart, ProductVariant productVariant, Size size) {
        String thumbnail = productVariant.getProduct()
                .getImages()
                .stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);

        CartItem item = CartItem.builder()
                .cart(cart)
                .productVariant(productVariant)
                .productId(productVariant.getProduct().getId())
                .productName(productVariant.getProduct().getName())
                .size(size)
                .sizeCode(size.getCode())
                .productThumbnail(thumbnail)
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

    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getItems().contains(cartItem)) {
            throw new RuntimeException("Cart item does not belong to this cart");
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }
}