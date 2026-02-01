package vn.phankyanh.shopbongda.repository;

import java.math.BigDecimal;
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
       
       @Query("""
       SELECT o FROM Order o
       LEFT JOIN FETCH o.payment p
       LEFT JOIN FETCH o.orderItems oi
       WHERE o.id = :id
       """)
       Optional<Order> findByIdWithPayment(@Param("id") Long id);

       @Query("SELECT DISTINCT o FROM Order o " +
              "LEFT JOIN FETCH o.orderItems oi " +
              "LEFT JOIN FETCH oi.productVariant p")
       List<Order> findAllWithOrderItems();

       // Lọc theo orderStatus
    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    // Lọc theo orderStatus + tìm theo fullName trong shippingAddress
    @Query("""
           SELECT o FROM Order o
           WHERE o.orderStatus = :orderStatus
           AND LOWER(o.shippingAddress.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Order> findByOrderStatusAndShippingFullName(@Param("orderStatus") OrderStatus orderStatus,
                                                     @Param("keyword") String keyword,
                                                     Pageable pageable);

    // Tìm kiếm theo fullName hoặc phone trong shippingAddress
    @Query("""
           SELECT o FROM Order o
           WHERE LOWER(o.shippingAddress.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR o.shippingAddress.phone LIKE CONCAT('%', :keyword, '%')
           """)
    Page<Order> search(@Param("keyword") String keyword, Pageable pageable);


    //dashboard
       @Query("SELECT COUNT(o) FROM Order o")
       Long countOrders();

       @Query("""
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM Order o
       WHERE o.orderStatus = 'COMPLETED'
       """)
       BigDecimal sumRevenue();

}
