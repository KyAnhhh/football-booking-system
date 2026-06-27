package vn.phankyanh.shopbongda.user.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.user.domain.ShippingAddress;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    Optional<ShippingAddress> findByIdAndUserId(Long id, Long userId);

    List<ShippingAddress> findAllByUserIdOrderByIdDesc(Long userId);
}
