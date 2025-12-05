package vn.phankyanh.shopbongda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Lấy tất cả CartItem theo cartId
    List<CartItem> findByCartId(Long cartId);

    // Kiểm tra xem cart item đã tồn tại với productId và cartId chưa
    CartItem findByCartIdAndProductId(Long cartId, Long productId);

    // Xóa tất cả item trong giỏ hàng
    void deleteByCartId(Long cartId);
}
