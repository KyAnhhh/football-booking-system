package vn.phankyanh.shopbongda.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequest {
        private OrderStatus status;
}
