package vn.phankyanh.shopbongda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.phankyanh.shopbongda.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
       List<Order> findByUserId(Long userId);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.productVariant p " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithOrderItems(@Param("id") Long id);
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.productVariant p")
    List<Order> findAllWithOrderItems();

}
