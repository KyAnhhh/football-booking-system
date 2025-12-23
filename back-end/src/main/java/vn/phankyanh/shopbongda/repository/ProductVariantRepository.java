package vn.phankyanh.shopbongda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    boolean existsByProductIdAndSizeId(Long productId, Long sizeId);

    List<ProductVariant> findByProductId(Long productId);
    java.util.Optional<ProductVariant> findByProductIdAndSizeId(Long productId, Long sizeId);
}
