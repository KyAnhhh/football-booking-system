package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.*;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.CartItemRequest;
import vn.phankyanh.shopbongda.dto.request.CartRequest;
import vn.phankyanh.shopbongda.dto.response.CartResponse;
import vn.phankyanh.shopbongda.mapper.CartMapper;
import vn.phankyanh.shopbongda.repository.CartRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;
import vn.phankyanh.shopbongda.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    public List<CartResponse> handleGetAllCart(){
        return this.cartRepository.findAll()
                .stream()
                .map(cartMapper::toCartResponse)
                .toList();
    }

    public CartResponse handleCreateCart(CartRequest request, String authHeader) throws ParseException {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Cart must have at least one item");
        }

        // Lấy user từ token
        String token = authHeader.replace("Bearer ", "");
        JwtInfo info = JwtService.parseToken(token);

        User user = userRepository.findById(info.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + info.getId()));


        Cart cart = new Cart();
        cart.setUser(user);

        List<CartItem> cartItems = new ArrayList<>();

        // Map từng CartItemRequest -> CartItem entity
        for (CartItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + itemReq.getProductId()));

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .totalQuantity(itemReq.getQuantity())
                    .priceAtAddedTime(product.getPrice())  // lấy giá tại thời điểm thêm
                    .build();

            cartItems.add(cartItem);
        }

        // Gán items vào cart
        cart.setItems(cartItems);

        // Tính tổng số lượng item trong giỏ
        int totalAmount = cartItems.stream()
                .mapToInt(CartItem::getTotalQuantity)
                .sum();

        cart.setTotalAmount(totalAmount);

        // Lưu cart (cascade ALL → cartItems tự lưu)
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    public CartResponse handleUpdateCart(Long id, CartRequest request){
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

        // Xóa items cũ (do orphanRemoval = true, clear() sẽ xóa các cart_item khỏi DB khi save)
        cart.getItems().clear();

        List<CartItem> newItems = new ArrayList<>();

        for (CartItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + itemReq.getProductId()));

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .totalQuantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                    .priceAtAddedTime(product.getPrice())
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

        return cartMapper.toCartResponse(saved);
    }

    public CartResponse handleGetCartById(Long id){
        Cart cart = this.cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return  cartMapper.toCartResponse(cart);
    }

    public void handleDeleteCart(Long id){
        if(!cartRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.cartRepository.deleteById(id);
    }
}
