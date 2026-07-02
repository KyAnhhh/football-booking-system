package vn.phankyanh.shopbongda.order.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_IdAndOrder_User_Id(Long orderId, Long userId);

    //dashboard
    @Query("""
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.orderStatus IN ('DELIVERED', 'COMPLETED')
    """)
    Long sumProductsSold();

    @Query("""
    SELECT COALESCE(SUM(oi.total), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.orderStatus IN ('DELIVERED', 'COMPLETED')
    """)
    BigDecimal getTotalRevenue();

    @Query("""
            select coalesce(sum(oi.total), 0)
            from OrderItem oi
            where oi.order.orderStatus in :statuses
            """)
    BigDecimal sumRecognizedRevenue(List<OrderStatus> statuses);

    @Query("""
            select coalesce(sum(oi.totalCost), 0)
            from OrderItem oi
            where oi.order.orderStatus in :statuses
            """)
    BigDecimal sumRecognizedCost(List<OrderStatus> statuses);

    @Query("""
            select coalesce(sum(oi.grossProfit), 0)
            from OrderItem oi
            where oi.order.orderStatus in :statuses
            """)
    BigDecimal sumRecognizedGrossProfit(List<OrderStatus> statuses);
}
