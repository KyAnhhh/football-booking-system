package vn.phankyanh.shopbongda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderStatus;

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

       // Lọc theo status
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Lọc theo status + tìm theo fullName trong shippingAddress
    @Query("""
           SELECT o FROM Order o
           WHERE o.status = :status
           AND LOWER(o.shippingAddress.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Order> findByStatusAndShippingFullName(@Param("status") OrderStatus status,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    // Tìm kiếm theo fullName hoặc phone trong shippingAddress
    @Query("""
           SELECT o FROM Order o
           WHERE LOWER(o.shippingAddress.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR o.shippingAddress.phone LIKE CONCAT('%', :keyword, '%')
           """)
    Page<Order> search(@Param("keyword") String keyword, Pageable pageable);

}
