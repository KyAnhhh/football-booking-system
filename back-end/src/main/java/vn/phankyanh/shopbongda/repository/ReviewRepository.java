package vn.phankyanh.shopbongda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
       SELECT r FROM Review r
       JOIN FETCH r.user
       WHERE r.product.id = :productId
       """)
    List<Review> findByProductIdWithUser(Long productId);
    
    Double findAverageRatingByProductId(Long productId);
    
    Long countByProductId(Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
