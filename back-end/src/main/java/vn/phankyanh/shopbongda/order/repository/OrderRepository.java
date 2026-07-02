package vn.phankyanh.shopbongda.order.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import org.springframework.data.repository.query.Param;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
       List<Order> findByUserId(Long userId);

       long countByOrderStatus(OrderStatus orderStatus);

       boolean existsByPaymentId(Long paymentId);

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

       @Lock(LockModeType.PESSIMISTIC_WRITE)
       @Query("""
       SELECT DISTINCT o FROM Order o
       LEFT JOIN FETCH o.payment p
       LEFT JOIN FETCH o.orderItems oi
       LEFT JOIN FETCH oi.productVariant pv
       WHERE o.id = :id
       """)
       Optional<Order> findByIdForUpdate(@Param("id") Long id);

       @Query("""
       SELECT o.id FROM Order o
       WHERE o.orderStatus = 'PENDING'
       AND o.paymentMethod = :paymentMethod
       AND o.createdAt < :cutoff
       """)
       List<Long> findPendingIdsCreatedBefore(
              @Param("paymentMethod") PaymentMethod paymentMethod,
              @Param("cutoff") LocalDateTime cutoff
       );

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

       @Query("""
       SELECT YEAR(COALESCE(o.updatedAt, o.orderDate)),
              MONTH(COALESCE(o.updatedAt, o.orderDate)),
              SUM(o.totalAmount)
       FROM Order o
       WHERE o.orderStatus = 'COMPLETED'
       GROUP BY YEAR(COALESCE(o.updatedAt, o.orderDate)),
                MONTH(COALESCE(o.updatedAt, o.orderDate))
       ORDER BY YEAR(COALESCE(o.updatedAt, o.orderDate)),
                MONTH(COALESCE(o.updatedAt, o.orderDate))
       """)
       List<Object[]> getMonthlyRevenue();

       @Query("""
       SELECT o FROM Order o
       JOIN FETCH o.shippingAddress s
       WHERE (:orderStatus IS NULL OR o.orderStatus = :orderStatus)
       ORDER BY o.orderDate DESC
       """)
       List<Order> findRecentDashboardOrders(
              @Param("orderStatus") OrderStatus orderStatus,
              Pageable pageable
       );

       // @Query("""
       //        SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
       //        FROM OrderItem oi
       //        WHERE oi.order.user.id = :userId
       //        AND oi.productVariant.product.id = :productId
       //        AND oi.order.orderStatus = vn.phankyanh.shopbongda.order.domain.OrderStatus.COMPLETED
       // """)
       // boolean existsDeliveredOrderProduct(Long userId, Long productId);
}
