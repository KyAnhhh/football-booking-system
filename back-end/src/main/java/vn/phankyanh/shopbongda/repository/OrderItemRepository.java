package vn.phankyanh.shopbongda.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.domain.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_IdAndOrder_User_Id(Long orderId, Long userId);

    //dashboard
    @Query("""
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.orderStatus = 'DELIVERED'
    """)
    Long sumProductsSold();

    @Query("""
    SELECT COALESCE(SUM(oi.total), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.orderStatus = 'DELIVERED'
    """)
    BigDecimal getTotalRevenue();

}
