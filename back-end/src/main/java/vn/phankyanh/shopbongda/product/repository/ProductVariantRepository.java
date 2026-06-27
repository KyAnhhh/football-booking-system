package vn.phankyanh.shopbongda.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    boolean existsByProductIdAndSizeId(Long productId, Long sizeId);

    List<ProductVariant> findByProductId(Long productId);
    java.util.Optional<ProductVariant> findByProductIdAndSizeId(Long productId, Long sizeId);

    List<ProductVariant> findAllByInventoryItemIdIsNullOrderByIdAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from ProductVariant v where v.id = :id")
    java.util.Optional<ProductVariant> findByIdForUpdate(@Param("id") Long id);
}
