package vn.phankyanh.shopbongda.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import vn.phankyanh.shopbongda.product.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    boolean existsByProductIdAndImageUrl(Long productId, String imageUrl);

    @Modifying
    @Transactional
    @Query("UPDATE ProductImage i SET i.isPrimary = false WHERE i.product.id = :productId")
    void resetPrimary(Long productId);
}
