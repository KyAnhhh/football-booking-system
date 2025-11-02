package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.mapper.OrderMapper;
import vn.phankyanh.shopbongda.mapper.ProductMapper;
import vn.phankyanh.shopbongda.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    public List<OrderResponse> handleGetAllOrder(){
        return this.orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse handleCreateOrder(OrderRequest request){
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }

        Order order = orderMapper.toOrder(request);
        order.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));

        order.setPayment(paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found")));

        order.setShippingAddress(shippingAddressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found")));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for(OrderItemRequest itemRequest : request.getItems()){
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id" +  itemRequest.getProductId()));

            BigDecimal price = product.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(price) // lưu giá tại thời điểm mua
                    .discount(0)  // có thể để mặc định hoặc lấy từ request
                    .build();

            orderItems.add(item);
            total = total.add(itemTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotal_amount(total);
        order.setOrderDate(LocalDateTime.now());

        this.orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public Order handleUpdateOrder(Long id, OrderRequest request){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: "+ id));

        // thiếu update sp mới và xóa sp cũ và tính tiền
        orderMapper.updateOrder(order , request);
        return orderRepository.save(order);

    }

    public OrderResponse handleGetOrderById(Long id){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return  orderMapper.toOrderResponse(order);
    }

    public void handleDeleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderRepository.deleteById(id);
    }
}
