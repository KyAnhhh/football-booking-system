package vn.phankyanh.shopbongda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.phankyanh.shopbongda.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
