package vn.phankyanh.shopbongda.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
       SELECT r FROM Review r
       JOIN FETCH r.user
       WHERE r.product.id = :productId
       """)
    List<Review> findByProductIdWithUser(Long productId);
    
    @Query("""
    SELECT AVG(r.rating)
    FROM Review r
    WHERE r.product.id = :productId
    """)
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    Long countByProductId(Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    
    boolean existsByOrderItemId(Long orderItemId);
}
