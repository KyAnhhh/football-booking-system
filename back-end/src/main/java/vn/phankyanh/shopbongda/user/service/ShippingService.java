package vn.phankyanh.shopbongda.user.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;

@Service
public class ShippingService {

    public BigDecimal calculate(List<OrderItem> orderItems,
                                ShippingAddress shippingAddress) {

        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalQty = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // rule hiện tại
        if (totalQty >= 10) {
            return BigDecimal.ZERO;          // free ship
        }

        if (totalQty >= 5) {
            return BigDecimal.valueOf(30000);
        }

        return BigDecimal.valueOf(50000);
    }
}
