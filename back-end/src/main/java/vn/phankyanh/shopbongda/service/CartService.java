package vn.phankyanh.shopbongda.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.request.CartRequest;
import vn.phankyanh.shopbongda.dto.response.CartResponse;
import vn.phankyanh.shopbongda.mapper.CartMapper;
import vn.phankyanh.shopbongda.repository.CartRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;
import vn.phankyanh.shopbongda.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    public List<CartResponse> handleGetAllCart() {
        return this.cartRepository.findAllWithItemsAndUser()
                .stream()
                .map(cartMapper::toCartResponse)
                .toList();
    }

    public CartResponse handleCreateCart(CartRequest request, String authHeader) throws ParseException {

    if (request.getItems() == null || request.getItems().isEmpty()) {
        throw new RuntimeException("Cart must have at least one item");
    }

    // Lấy user từ JWT
    String token = authHeader.replace("Bearer ", "");
    JwtInfo info = JwtService.parseToken(token);

    User user = userRepository.findById(info.getId())
            .orElseThrow(() -> new RuntimeException("User not found with id " + info.getId()));

    // Load cart cũ nếu có, không thì tạo mới
    Cart cart = cartRepository.findByUserIdWithItems(user.getId())
            .orElseGet(() -> {
                Cart c = new Cart();
                c.setUser(user);
                c.setItems(new ArrayList<>());
                return c;
            });

    // Merge items
    for (CartItemRequest itemReq : request.getItems()) {
        ProductVariant productVariant = productVariantRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + itemReq.getProductId()));

        // Check if product already exists in cart
        CartItem exItem = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProductId() != null && item.getProductId().equals(productVariant.getId())) {
                exItem = item;
                break;
            }
        }

        if (exItem != null) {
            // Update existing item quantity
            exItem.setTotalQuantity(
                    exItem.getTotalQuantity() + itemReq.getQuantity()
            );
        } else {
            // Create new cart item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .productId(productVariant.getProduct().getId())
                    .productName(productVariant.getProduct().getName())
                    .totalQuantity(itemReq.getQuantity())
                    .priceAtAddedTime(productVariant.getPrice())
                    .build();

            cart.getItems().add(newItem);
        }
    }

    // Tính lại tổng số lượng
    int totalAmount = cart.getItems().stream()
            .mapToInt(CartItem::getTotalQuantity)
            .sum();

    cart.setTotalAmount(totalAmount);

    // Lưu
    Cart saved = cartRepository.save(cart);

    // Reload với fetch join để mapper không bị null field
    Cart reloaded = cartRepository.findByIdWithItemsAndUser(saved.getId()).orElse(saved);

    return cartMapper.toCartResponse(reloaded);
}


    public CartResponse handleUpdateCart(Long id, CartRequest request) {
        Cart cart = this.cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));

        // Nếu request items null/empty => giữ nguyên (hoặc ném lỗi tuỳ yêu cầu)
        if (request.getItems() == null || request.getItems().isEmpty()) {
            // Nếu bạn muốn cho phép xóa hết items when empty request, uncomment:
            // cart.getItems().clear();
            // cart.setTotalAmount(0);
            // cart.setUpdatedAt(LocalDateTime.now());
            // cartRepository.save(cart);
            // return cartMapper.toCartResponse(cart);

            throw new RuntimeException("Cart must have at least one item");
        }

        // Xóa items cũ (do orphanRemoval = true, clear() sẽ xóa các cart_item khỏi DB
        // khi save)
        cart.getItems().clear();

        List<CartItem> newItems = new ArrayList<>();

        for (CartItemRequest itemReq : request.getItems()) {
            ProductVariant productVariant = productVariantRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + itemReq.getProductId()));

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .productId(productVariant.getProduct().getId())
                    .productName(productVariant.getProduct().getName())
                    .totalQuantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                    .priceAtAddedTime(productVariant.getPrice())
                    .build();

            newItems.add(item);
        }

        cart.setItems(newItems);

        int totalAmount = newItems.stream()
                .mapToInt(CartItem::getTotalQuantity)
                .sum();

        cart.setTotalAmount(totalAmount);
        cart.setUpdatedAt(LocalDateTime.now());

        Cart saved = cartRepository.save(cart);
        Cart reloaded = cartRepository.findByIdWithItemsAndUser(saved.getId()).orElse(saved);
        return cartMapper.toCartResponse(reloaded);
    }

    public CartResponse handleGetCartById(Long id) {
        Cart cart = this.cartRepository.findByIdWithItemsAndUser(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return cartMapper.toCartResponse(cart);
    }

    public void handleDeleteCart(Long id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Not found with id: " + id);
        }
        this.cartRepository.deleteById(id);
    }
}
