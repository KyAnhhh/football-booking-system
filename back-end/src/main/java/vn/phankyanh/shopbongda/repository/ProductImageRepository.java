package vn.phankyanh.shopbongda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.phankyanh.shopbongda.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    @Modifying
    @Query("UPDATE ProductImage i SET i.isPrimary = false WHERE i.product.id = :productId")
    void resetPrimary(Long productId);
}
