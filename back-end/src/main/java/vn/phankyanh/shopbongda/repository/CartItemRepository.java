package vn.phankyanh.shopbongda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.domain.Size;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartAndProductVariantAndSize(Cart cart, ProductVariant productVariant, Size size);

}
